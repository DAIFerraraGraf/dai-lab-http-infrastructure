# dai-lab-https

## Step 1: Static Web site
To start, you will create a static Web site that will be served by an Nginx Web server running in a Docker container.
We created a folder named `website` that contains everything's needed for the website. To modify the website, you can 
modify the files in this folder.

### nginx.conf
This file contains the configuration of the nginx server. It is a simple configuration that serves the files in the
`/usr/share/nginx/html` folder. The server will start on port 80 and display the files in the `website` folder. The first page will
be `index.html`.

### Dockerfile
This file contains the instructions to build the docker image. It starts from the `nginx` image and copies the `website` folder
in the `/usr/share/nginx/html` folder of the image. It also copies the `nginx.conf` file in the `/etc/nginx/conf.d` folder of the
image.

To avoid conflict while building the image, we remove de default configuration file of nginx in the `/etc/nginx/conf.d` folder.
## Step 2: Docker compose


## Step 3: HTTP API server


## Step 4: Reverse proxy with Traefik


## Step 5: Scalability and load balancing


## Step 6: Load balancing with round-robin and sticky sessions



## Step 7: Securing Traefik with HTTPS


## Optional steps

### Optional step 1: Management UI

The goal of this step is to deploy or develop a Web app that can be used to monitor and update your Web infrastructure dynamically. You should be able to list running containers, start/stop them and add/remove instances.

    you use an existing solution (search on Google)
    for extra points, develop your own Web app. In this case, you can use the Dockerode npm module (or another Docker client library, in any of the supported languages) to access the docker API.

Acceptance criteria

    You can do a demo to show the Management UI and manage the containers of your infrastructure.
    You have documented how to use your solution.
    You have documented your configuration in your report.

### Optional step 2: Integration API - static Web site

This is a step into unknow territory. But you will figure it out.

The goal of this step is to change your static Web page to periodically make calls to your API server and show the results in the Web page. You will need JavaScript for this and this functionality is called AJAX.

Keep it simple! You can start by just making a GET request to the API server and display the result on the page. If you want, you can then you can add more features, but this is not obligatory.

The modern way to make such requests is to use the JavaScript Fetch API. But you can also use JQuery if you prefer.
Acceptance criteria

    You have added JavaScript code to your static Web page to make at least a GET request to the API server.
    You can do a demo where you show that the API is called and the result is displayed on the page.
    You have documented your implementation in your report.
