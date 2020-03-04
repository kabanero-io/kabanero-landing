![](src/main/content/img/Kabanero_Logo_Hero.png)

# Introduction
Console Application for Kabanero

![](src/main/content/img/kabanero-landing-screenshot-0_4_0.png)

## Contributing to the landing page

Please [view our contribution guidelines](https://github.com/kabanero-io/kabanero-landing/blob/master/CONTRIBUTING.md) for the Kabanero.io console.

## Community
- [Kabanero on Twitter](https://twitter.com/Kabaneroio)
- [kabanero tag on stackoverflow](https://stackoverflow.com/questions/tagged/kabanero)
- [Kabanero on Slack](https://ibm-cloud-tech.slack.com/messages/kabanero)
   - [Slack channel request](https://slack-invite-ibm-cloud-tech.mybluemix.net)

## Run in dev mode
1. Package the jekyll frontend. From the repository's root directory:
   * ```
      export PAT=<YOUR_GITHUB_PERSONAL_ACCESS_TOKEN>
      ./scripts/build_jekyll_maven.sh
      ```
      * You can get a github PAT from your GitHub account. [More information about Personal Access Tokens](https://help.github.com/en/github/authenticating-to-github/creating-a-personal-access-token-for-the-command-line)
1. Run: `mvn liberty:dev`
   * The app will be available at `https://localhost:9443`
   * This will run the server in [dev mode](https://github.com/OpenLiberty/ci.maven/blob/master/docs/dev.md#dev) which provides hot reloading for server side changes. Updates to the frontend files (html, css, js) won't be reloaded in this current setup. Instead, you want to develop the frontend with hot reloading you can run `./scripts/jekyll_serve_dev.sh` which will hot reload for frontend updates only.

## Docker

### Build

```
./ci/build.sh
```

### Run for development purposes
For production you do not need to mount the kube config, or add the `-u 0` parameter.

1. Ensure Kabanero is installed on your cluster
   * See [Installing Kabanero Foundation](https://kabanero.io/docs/ref/general/installing-kabanero-foundation.html) for help installing Kabanero
1. Login to your cluster using the `oc` CLI.
1. Run the docker image produced from the build step above:

   ```
   docker run --rm -p 9443:9443 -v ~/.kube/config:/root/.kube/config -u 0 landing:latest
   ```

## Configure GitHub OAuth

To manage your stacks via this console you must configure OAuth to your GitHub. When OAuth is configured properly, you will see "Manage Stacks" button on the instance page inside the stacks tile UI.

### Configure GitHub OAuth for production

See [Configuring the Kabanero Console with OAuth](https://kabanero.io/docs/ref/general/configuration/console-oauth.html)

### Configure GitHub OAuth for local development

If you would like to setup GitHub OAuth for local development follow these steps:

1. Create a GitHub OAuth App in your GitHub Organization that has your stack hub. See step 1 in [Create a GitHub OAuth application](#create-a-github-oauth-application) for more info.
   * This will give you your **GitHub OAuth ID** and **GitHub OAuth secret** for the configuration below.
1. Fork and clone this repository.
1. Create an `oauth` directory in the root of this repository.
1. Create 3 files **inside the oauth directory:**
   1. `clientID` and place your GitHub OAuth ID in it and save.
   1. `clientSecret` and place your GitHub OAuth secret in it and save.
   1. `.env`, and copy the below env vars into it. Change the values if you want to point to a different GitHub server.
      * 
         ```
         USER_API=https://api.github.com/user
         TOKEN_ENDPOINT=https://github.com/login/oauth/access_token
         AUTHORIZATION_ENDPOINT=https://github.com/login/oauth/authorize
         WEBSITE=https://github.com
         ```
      * For GitHub Enterprise both `TOKEN_ENDPOINT` and `AUTHORIZATION_ENDPOINT` should be the same (except the hostname) and `USER_API` should be `https://<YOUR_HOSTNAME>/api/v3/user`
1. Build and run the image.
   1. Build the image
      * From the root of this repo run: `./ci/build.sh`
   1. Run the image
      * `cd` to the root of this repository then run: `docker run --rm -p 9443:9443 -v ~/.kube/config:/root/.kube/config -v "$(pwd)/oauth":/etc/oauth --env-file "$(pwd)/oauth/.env" -u 0 landing:latest`
