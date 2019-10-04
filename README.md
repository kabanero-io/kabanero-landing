![](src/main/content/img/Kabanero_Logo_Hero.png)

# Introduction
Landing page for the Kabanero OKD Console.

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
docker run -p 9443:9443 -v ~/.kube/config:/root/.kube/config -u 0 landing:latest
```