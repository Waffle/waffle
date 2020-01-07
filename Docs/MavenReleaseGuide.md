Introduction
------------

This is a guide for WAFFLE developers hoping to publish a release to Maven Central.  It summarizes the high-level process and points out the relevant WAFFLE-specific points, but does not go into details about particular tools, as there are other documentation sources that are more relevant for that.  The majority of this document is based on the [Sonatype OSS Maven Repository Usage Guide][OSSGuide].

Building with Maven
-------------------

In order to build with [Maven][], you'll need Java 8+ and [Maven][] 3.6.3+.  Download and install them, and then run `mvn --version` to check that it's working.  If you don't already have it, also get Git and a clone of the WAFFLE repository.  Once you have this, run `mvn package` in the `Source/JNA/waffle-parent` directory.  This command compiles, unit tests, and JARs all of the WAFFLE components that are currently Maven-enabled.

If you don't want to run the unit tests (which can be useful if you're trying to compile on a non-Windows platform), use `mvn package -DskipTests=true` instead.  This isn't recommended for release builds.

For local testing, you can use `mvn install`.  This packages the components and installs the resulting JARs into a repository local to your machine, but doesn't publish them anywhere else.  For more information on building software with Maven, see the [Running Maven](https://maven.apache.org/run-maven/index.html).

One-Time Release Setup
----------------------

In order to perform Maven releases, there's some extra steps needed beyond what's needed to build WAFFLE.

First, make sure you have a [GPG][] client installed and on your command-line path.  If you don't already have one, create a [GPG][] key pair and distribute your public key to hkp://pool.sks-keyservers.net/.  See [How To Generate PGP Signatures With Maven](http://central.sonatype.org/pages/working-with-pgp-signatures.html) for more information.

Next, you need to get access to the Sonatype OSS repositories.  This is covered as steps 2-3 in the [Sonatype OSS Maven Repository Usage Guide][OSSGuide].  The correct groupId to use is "com.github.dblock".

After you have your account all set up, configure your Maven settings.xml (generally in a .m2 directory within your user home directory) like this (pulled from [Sonatype OSS Maven Repository Usage Guide][OSSGuide] section 7a.1):

    <settings>
      ...
      <servers>
        <server>
          <id>sonatype-nexus-snapshots</id>
          <username>your-jira-id</username>
          <password>your-jira-pwd</password>
        </server>
        <server>
          <id>sonatype-nexus-staging</id>
          <username>your-jira-id</username>
          <password>your-jira-pwd</password>
        </server>
      </servers>
      ...
    </settings>

In order to perform mvn release, there's some extra steps needed byond what's needed to build WAFFLE.

First, follow this link and get yourself setup with ssh on github first [github-ssh-keys].  If ssh-add does not work and fails 
with error 'Could not open a connection to your authentication agent' but the agent looks right, enter 'eval $(ssh-agent)', then try the ssh-add again.

Deploying Snapshots
-------------------

Snapshot versions allow sharing code that is under development (not yet released).  These versions may change over time (unlike released versions) and are not published to Maven Central.  To use a snapshot version, a user will need to configure their build tool to use the correct snapshot repository, which in this case is `https://oss.sonatype.org/content/repositories/snapshots/`.  See the [POM Syntax](https://www.sonatype.com/books/mvnref-book/reference/pom-relationships-sect-pom-syntax.html) from Maven: The Complete Reference for more information.

To publish a snapshot, run `mvn clean deploy`.  See section 7a.2 of the [Sonatype OSS Maven Repository Usage Guide][OSSGuide] for more information.

Releasing to Maven Central
--------------------------

Releasing to Maven Central via the Sonatype OSS Repository is a two-phase process.  First, you assemble the desired artifacts in a staging repository, then you finalize the release.  The relevant sections of the [Sonatype OSS Maven Repository Usage Guide][OSSGuide] are 7a.3 and 8a.

To build the artifacts and upload them to a new staging repository, run the following commands.  These make use of the [Maven Release Plugin][maven-release-plugin], which is rather complex.  It will prompt you for various information related to the release, modify the POM files to update the version, and perform Git operations on your behalf.  It is recommended to read its documentation before proceeding, and consider doing a dry run (as documented in the [usage page](https://maven.apache.org/plugins/maven-release-plugin/usage.html) before proceeding with the actual release.

*** DO NOT USE ECLIPSE TO RUN THESE AS IT HANGS, USE COMMAND LINE ***

    mvn release:clean
    mvn release:prepare
    mvn release:perform

Once you've succeeded in finishing those steps, log in to [Sonatype OS](https://oss.sonatype.org/), and perform the following steps.

*   Go to [Staging Repositories](https://oss.sonatype.org/index.html#stagingRepositories).
*   Select the staging repository at the bottom that was created by the release process, it should have a `com.github.dblock` profile.
*   Click the *Close* button in the toolbar. This should take a bit of time to complete, you may need to *Refresh*.
*   If there are any problems reported, fix them.
*   Click on the closed staging repository.
*   Examine the *Content* tab. Right click on artifacts to download them.
*   Test the downloaded artifacts to make sure that the contents of the staging repository are what you want to release.
*   Click the *Release* button in the toolbar.
*   If there are any problems reported, fix them.
*   The release should now be in the *Releases* repository.
*   For projects that have Maven Central synchonization enabled, their artifacts in the *Releases* repository are synched to Maven Central every two hours. The *very first time* the WAFFLE project published required a comment on the setup JIRA ticket to get synch enabled.

Deploying in event of release plugin failure
--------------------------------------------

The release plugin can be quite tricky.  Recently, we ran into issues trying to use it where it would change from snapshot to the release version but then failed the release:prepare.  As such, there is a way to get around this issue without much more work.

If release plugin fails and versions are all essentially flagged for release version.  Simply use this then follow the remainder of the release process.

   mvn deploy -Prelease

After deployment in this case, make sure to set everything manually back to next snapshot release.

Releasing the Site Page to gh-pages
-----------------------------------

Setup deployment by adding this to your .m2 settings.xml file.

    <server>
      <id>gh-pages</id>
      <password>--replace-with-your-password--</password>
    </server>

Generate the site pages off waffle-parent pom by running.

    mvn site

Deploy the site pages off waffle-parent pom by running.

    mvn site:deploy

Deploy will use ssh to perform the deployment.  This takes some time.  Be patient!!!

[OSSGuide]: http://central.sonatype.org/pages/ossrh-guide.html
[Maven]: https://maven.apache.org/
[gpg]: https://www.gnupg.org/
[maven-release-plugin]: https://maven.apache.org/plugins/maven-release-plugin/
[github-ssh-keys]: https://help.github.com/articles/generating-ssh-keys/
