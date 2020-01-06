![](src/main/content/img/Kabanero_Logo_Hero.png)

# Introduction
Landing page for the Kabanero OKD Console.

![](src/main/content/img/kabanero-landing-screenshot.png)

## Contributing to the landing page

Please [view our contribution guidelines](https://github.com/kabanero-io/kabanero-landing/blob/master/CONTRIBUTING.md) for the Kabanero.io landing page.

## Community
- [Kabanero on Twitter](https://twitter.com/Kabaneroio)
- [kabanero tag on stackoverflow](https://stackoverflow.com/questions/tagged/kabanero)
- [Kabanero on Slack](https://ibm-cloud-tech.slack.com/messages/kabanero)
   - [Slack channel request](https://slack-invite-ibm-cloud-tech.mybluemix.net)

## Build

```
./ci/build.sh
```

## Run Locally
1. Ensure Kabanero is installed on your OKD cluster
   * See [Installing Kabanero Foundation](https://kabanero.io/docs/ref/general/installing-kabanero-foundation.html) for help installing Kabanero
1. Login to your OKD cluster
1. Run the docker image produced from the build step above

```
docker run --rm -p 9443:9443 -v ~/.kube/config:/root/.kube/config -u 0 landing:latest
```

## Configure GitHub OAuth

To manage your collections via this console you must configure OAuth to your GitHub. When OAuth is configured properly, you will see "Manage Collections" button on the instance page inside the collections tile UI.

### Configure GitHub OAuth for production

To start, an OAuth GitHub app will need to be created in the same GitHub organization that your collection hub is in (your forked collection repo)
   * If the OAuth GitHub app and the Collections repository **need** to be in different GitHub orgs, then the OAuth GitHub app can request access to data in the org with the collections. For more infomrmation see the GitHub doc - [OAuth App Access and Restrictions](https://help.github.com/en/github/setting-up-and-managing-organizations-and-teams/about-oauth-app-access-restrictions)

1. In your GitHub organization, create an OAuth Application in **Settings -> OAuth Apps**
   * The application name can be anything, a suggestion is: `kabanero console`.
   * Homepage URL can be anything for now since the application doesn't exist yet, you can come back and fill this in
   * Authorization callback URL is `https://<YOUR_HOST_IP_PORT>/ibm/api/social-login/redirect/githubLogin`
      * If you do not know the full application host and port yet, you can come back and fill this in later.
   * Note the `Client ID` and `Client Secret` as you will need this when installing your Kabanero CRD

1. In the Kabanero namespace create a secret named: `kabanero-github-oauth-secret`
   * Note: The secret name must be exact.

1. Add two key value pairs to the secret. Note: the key names must be exact.
   * key: `clientID` and the value is your client ID from GitHub.
   * key: `clientSecret` and the vlue is your client secret from GitHub.

1. Restart (delete) the kabanero landing pod.

### Configure GitHub OAuth for local development

If you would like to setup GitHub OAuth for local development follow these steps:

1. Create a GitHub OAuth App in your GitHub Organization that has your collection hub. See step 1 in [Create a GitHub OAuth application](#create-a-github-oauth-application) for more info.
   * This will give you your **GitHub OAuth ID** and **GitHub OAuth secret** for the configuration below.
1. Fork and clone this repository.
1. Create an `oauth` directory in the root of this repository.
1. Create 3 files **inside the oauth directory:**
   1. `consoleOAuthId` and place your GitHub OAuth ID in it and save.
   1. `consoleOAuthSecret` and place your GitHub OAuth secret in it and save.
   1. `.env`, and copy the below env vars into it. Change the values if you want to point to a different GitHub server.
      * 
         ```
         USER_API=https://api.github.com/user/
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