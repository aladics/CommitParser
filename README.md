# Commit Parser
A simple tool to mine before and after states of methods in commits, that are marked as vulnerability fixes in the project-kb repo. 

## Prerequisities
The following must be installed beforehand:
  * Java SE 15
  
## Installation and run
This tool uses the [JavaParser](https://javaparser.org/) tool to parse the ast from the java files, and the a [Java wrapper for the GitHub API](https://github-api.kohsuke.org/) to parse the commits. For the GitHub API to work, an oath token must be provided as stated in the official documentation:
  * Create file `~/.github`
  * Place the text `oauth=<TOKEN>` into it, where <TOKEN> is substituted with your own GitHub oath access token.

Compilation, building is done with the help of Gradle:
  * `gradlew run --args="<OUTPUT CSV PATH> <PROJECT_KB/STATEMENTS PATH>"`
  
`<OUTPUT CSV PATH>` should be the path to a csv where the results will be written.  
`<PROJECT_KB/STATEMENTS PATH>` should be the path to the `statements folder` in the `vulnerability-data` branch of [`project-kb`](https://github.com/SAP/project-kb/tree/vulnerability-data) repo.
