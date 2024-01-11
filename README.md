# dai-lab-https

## Step 1: Static Web site

To start, we created a static Web page with its own folder.
We created a folder named `website` that contains everything's needed for the website. To modify the website, we can
modify the files in this folder.

### nginx.conf

This file contains the configuration of the nginx server. It is a simple configuration that serves the files in the
`/usr/share/nginx/html` folder. The server will start on port 80 and display the files in the `website` folder. The
first page will be `index.html`. We always start the server on port 80 because we are not using HTTPS for the moment and
afterward, we will use a reverse proxy to redirect the HTTP connection to an HTTPS connection and communicate with the
server directly on port 80.

### Dockerfile

This file contains the instructions to build the docker image. It starts from the `nginx` image and copies the `website`
folder in the `/usr/share/nginx/html` folder of the image. It also copies the `nginx.conf` file in
the `/etc/nginx/conf.d` folder of the image.

To avoid conflict while building the image, we remove de default configuration file of nginx in the `/etc/nginx/conf.d`
folder.

## Step 2: Docker compose
### Docker compose file for step 2
This is the docker compose file we used for step 2. It contains only the nginx service.
```
version: '3.8'
services:
  nginx:
    build: nginx
    ports:
      - "8080:80"  # To map the port 80 of the container to the port 8080 of the host
```

### Docker compose file
This is our final  docker compose file, containing all the services we used for this project.
```
version: '3.8'
services:

  reverse_proxy:
    image: traefik
    command:
    - --providers.docker
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      - ./traefik/ssl:/etc/traefik/certificates
      - ./traefik/traefik.yaml:/etc/traefik/traefik.yaml
    ports:
      - "80:80" # Web sites
      - "443:443" # Web sites
      - "8080:8080" # Traefik dashboard

  javaserver:
    image: dai-lab-https-java-server-1
    expose:
      - 80
    build:
      context: .
    labels:
      - traefik.http.routers.javaserver.rule=Host(`localhost`) && PathPrefix(`/api`)
      - traefik.http.services.javaserver.loadbalancer.sticky=true
      - traefik.http.services.javaserver.loadbalancer.sticky.cookie.name=StickyCookie
      - traefik.http.services.javaserver.loadbalancer.sticky.cookie.secure=true
      - traefik.http.routers.javaserver.entrypoints=https
      - traefik.http.routers.javaserver.tls=true

    deploy:
      replicas: 2

  portainer:
    image: portainer/portainer-ce:latest
    ports:
      - 9443:9443
      - 9000:9000
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      - portainer_data:/data
    restart: unless-stopped

  nginx:
    build: nginx
    labels:
      - traefik.http.routers.nginx.rule=Host(`localhost`)
      - traefik.http.routers.nginx.entrypoints=https
      - traefik.http.routers.nginx.tls=true
    deploy:
      replicas: 2

```

### Docker compose file explanation
To start the docker compose, we can use the following command:
```bash
docker compose up
docker compose up -d # To start the docker compose in detached mode
```
To rebuild the docker compose, we can use the following command:
```bash
docker compose build
docker compose up --build # To rebuild the docker compose and start it
```
To stop the docker compose, we can use CTRL-C if docker compose is not in the background or the following command:
```bash
docker compose stop
docker compose down # To stop and remove the docker compose
```


## Step 3: HTTP API server
### Overview
We jointly integrated the database project with the DAI project for the graphical interface part to display the data.
The database project is a garbage center management system. It allows to manage the employees and the waste collection.

In our graphical interface, we have two views. Each view is displayed in function of the role of the login user. We
have
three roles: Employe, Secrétaire and Responsable. The role is defined in the database project. The login is also defined
in the database project. Every user have the same password: `password`.
Here is a small list of some of the users:

| Login    | Password | Last name | First name | Role        |
|----------|----------|-----------|------------|-------------|
| jdoe     | password | Doe       | John       | Responsable |
| lhussain | password | Hussain   | Lucas      | Secrétaire  |
| asmith   | password | Smith     | Alice      | Employé     |

The first one is the employes view. It allows to see the list of the employees and to add, modify or delete an employee.

The second one is the waste collection view. It allows to see the waste collection and to add, modify or delete a waste
collection.

To make the project more realistic, we added a database to store the data. We used the PostgreSQL database.

### API
To communicate with the api, we used the url `localhost/api`. We tested each CRUD operation with the software Bruno and 
save the tests inside the `Bruno` folder.

## Step 4: Reverse proxy with Traefik

To start, we have to add the traefik service in the docker compose file. We also have to add the labels to the services
we want to be accessible from the outside. In our case, we want to access the nginx server and the javaserver. We also
want to access the traefik dashboard.

```
  reverse_proxy:
    image: traefik
    command:
    - --api.insecure=true # Enable Traefik dashboard
    - --providers.docker
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
    ports:
      - "80:80" # Web sites
      - "8080:8080" # Traefik dashboard
```

The first line is the name of the service. The second line is the image we want to use. The third line is the command
we want to execute when the container is started. 

In our case, we want to enable the traefik dashboard and we want to
use the docker provider. The docker provider allows traefik to discover the services running in the docker compose file.
The fourth line is the volume we want to mount to allow traefik to access the docker socket. The fifth line is the ports
we want to expose. In our case, we want to expose the port 80 for the websites and the port 8080 for the traefik
dashboard.

In this specific case, /var/run/docker.sock:/var/run/docker.sock allows Traefik to access the Docker socket of the host
system from the container. The Docker socket (docker.sock) is an access point to the Docker API that allows Traefik to
interact dynamically with the Docker engine to retrieve information about running containers, networks, etc.

By using this volume, Traefik can monitor the status of Docker containers, discover newly created or deleted services,
and dynamically update its configuration based on changes in the Docker environment, allowing it to redirect traffic to
the appropriate services dynamically and automatically.

```
nginx:
    build: nginx
    labels:
        - traefik.http.routers.nginx.rule=Host(`localhost`)
        
javaserver:
    image: dai-lab-https-java-server-1
    expose:
      - 80
    build:
      context: .
    labels:
      - traefik.http.routers.javaserver.rule=Host(`localhost`) && PathPrefix(`/api`)
```

In our case, we want to access the nginx server and the javaserver. We also want to access the traefik dashboard. To do
so, we must add the labels to the services we want to be accessible from the outside. For the nginx server, we want
to access it with the url `localhost`. So we add the label `traefik.http.routers.nginx.rule=Host(`localhost`)`.

For the javaserver, we want to access it with the url `localhost/api`. So we add the label
`traefik.http.routers.javaserver.rule=Host(`localhost`) && PathPrefix(`/api`)`. To be able to access the javaserver, we
also have to expose the port 80.

## Step 5: Scalability and load balancing
To let Traefik discover how many instances of a service are running, we have to add the following label to the service:
```
    command:
    - --providers.docker
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
```
By doing this, we are saying to Traefik to use the docker provider to discover the services running inside the docker and
to use the docker socket to access the docker engine. With this configuration, the socket will be mounted (and not copied) 
inside the container and make possible the dynamic discovery of the services running inside the docker compose file.


To start several server instance, we use the `deploy` command inside the docker compose file

```
  nginx:
    build: nginx
    labels:
      - traefik.http.routers.nginx.rule=Host(`localhost`)
    deploy:
      replicas: 2
```

For testing Treafik, we can use this command to add or remove some instance of each service running inside the docker
compose:
```
docker compose up --scale nginx=6 -d
```
This command will set the number of instances for nginx to 6. If the actual number of instances is lower, docker will add
new instances. And if it's greater, it will remove instances.

To check if its truly working, we can use the traefik dashboard. We can access it with the url `localhost:8080` and check
the number of instance for each service that Traefik is managing. We should see the number of instance that we set earlier.

## Step 6: Load balancing with round-robin and sticky sessions

To enable sticky-sessions, we just have to put these lines inside the configuration of the javaserver inside the docker
file :

```
    labels:
      - traefik.http.services.javaserver.loadbalancer.sticky=true
      - traefik.http.services.javaserver.loadbalancer.sticky.cookie.name=StickyCookie
```
We don't have to specify a sticky cookie for the java server because our web page is stateless. So, we don't need to keep
the session of the user on the same server.

// TODO
To test the sticky sessions, we added logs inside the java server. We can see the logs inside the command line and check 
with the docker header which instance of the java server is responding.


## Step 7: Securing Traefik with HTTPS

To secure Traefik with HTTPS, we must modify the docker compose file to mount the new configuration file and map the
new volume for the certificates. We also need to add the 443 port to be able to make an HTTPS connection.

```
  reverse_proxy:
    image: traefik
    command:
    - --providers.docker
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      - ./traefik/ssl:/etc/traefik/certificates
      - ./traefik/traefik.yaml:/etc/traefik/traefik.yaml
    ports:
      - "80:80" # Web sites
      - "443:443" # Web sites
      - "8080:8080" # Traefik dashboard
```

To enable the HTTPS connection, we have to modify the other services running in the docker compose file. We have to add
the labels to enable the HTTPS connection and to specify the entrypoint to use.

```
labels:
    - traefik.http.routers.javaserver.entrypoints=https
    - traefik.http.routers.javaserver.tls=true
```

Inside the configuration file of Traefik, we have to specify the certificates to use and the entrypoints to use. We also
have to specify the port to use for the HTTPS connection and the port to redirect the HTTP connection to an HTTPS
connection.

```
# Define providers
providers:
  docker:
    endpoint: "unix:///var/run/docker.sock"
    exposedByDefault: true


# Define entrypoints
entryPoints:
  http:
    address: ":80"
    http:
      redirections:
        entryPoint:
          to: https
          scheme: https
          permanent: true
  https:
    address: ":443"

# Configure the API
api:
  insecure: true
  dashboard: true

tls:
  certificates:
    - certFile: "/etc/traefik/certificates/cert.pem"
      keyFile: "/etc/traefik/certificates/key.pem"
```

To generate the certificates, we can use the following command inside a linux terminal:

```
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -sha256 -days 365
```

It will generate the certificates and the key of a validity of one year. We must put them inside the
folder `traefik/ssl`.

## Optional steps

### Optional step 1: Management UI

To manage easily the docker containers, we can use the portainer service. To do so, we have to add the following service
inside the docker compose file:

```
  portainer:
    image: portainer/portainer-ce:latest
    ports:
      - 9443:9443
      - 9000:9000
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      - portainer_data:/data
    restart: unless-stopped
```

We are mapping the port 9443 for the HTTPS connection and the port 9000 for the HTTP connection. We are also mapping the
docker socket to allow portainer to manage the docker containers. We are also mapping a volume to allow portainer to
save the data.

With this configuration, we can access the portainer dashboard with the url `localhost:9000` or `localhost:9443`. With
portainer, we can manage the docker containers, the docker images, the docker volumes and the docker networks easily,
without coding manually a web interface.

### Optional step 2: Integration API - static Web site
As we said earlier, we integrated the database project with the DAI project for the graphical interface part to display
the data. It includes some javascript code to display the data and make all the CRUD operations.