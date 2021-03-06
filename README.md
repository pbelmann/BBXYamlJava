# BBXYamlJava
A Module originally planed for SITB. Converts particular .yaml-files (describing lists of applications) into abstract Java - objects and other particular Java - obects in biobox.yaml  - files.

In this readme I will show you to integrate and use this package. After that, i will describe how to host your own project. 

# Integration and useage 
## Step 1: How to integrate this package into a maven project.

Go into your .pom - file and add 
~~~XML
 <repository>
    < id>bbxyamljava-repository</id>
    <url>https:/github.org/MarkUgarov/BBXYamlJava/raw/mvn-repo</url>
</repository>
~~~
to the repositories-section. Also add
~~~XML
<dependency>
    <groupId>com.mugarov </groupId>
    <artifactId>BBXYamlJava</artifactId>
    <version>v0.1.0</version>
    <type>jar</type>
</dependency>
~~~
to the dependencies- section. Please check if there is a better version for you than v0.1.0 and just put its name in the version- element. 

## Step 2: How to use.

For practical examples: See JUnit-Tests in this package. 
You will need to import import yamlparse.BBXYamlJava first, then make an instance out of this class. 
For parsing .yaml-formated files into the abstract dataformat get a new instance of yamlparse.YamlInparse  by using #BBXYamlJava.getNewYamlInparser(),
for outparsing into a biobox.yaml-file from an abstract datatype use  #BBXYamlJava.getNewYamlOutparser(). 
___

#How to host your own Maven-Repository on github:

There are two ways for this: 

1. Look at http://stackoverflow.com/questions/14013644/hosting-a-maven-repository-on-github

2. Similar to that 

 - first integrate

~~~XML
<distributionManagement>
    <repository>
        <id>internal.repo</id>
        <name>Temporary Staging Repository</name>
        <url>file://${project.build.directory}/mvn-repo</url>
    </repository>
</distributionManagement>

<plugins>
    <plugin>
        <artifactId>maven-deploy-plugin</artifactId>
        <version>2.8.1</version>
        <configuration>
            <altDeploymentRepository>internal.repo::default::file://${project.build.directory}/mvn-repo</altDeploymentRepository>
        </configuration>
    </plugin>
</plugins>
~~~
into your .pom- file. Also do not forget to add your groupId and the version. It could look like 
~~~XML
    <groupId>com.myName</groupId>
    <artifactId>ProjectName</artifactId>
    <version>version1.0</version>
    <packaging>jar</packaging>
    <name>AlsoTheProjectName</name>
    <url>http://maven.apache.org</url>
    <properties>
      ...
~~~

- Use mvn clean deploy (or just click "Clean and Build Project" in netbeans). 
- Make a directory like #projectPath/hostdata you want to use for hosting (lets say it's the $HOST now). 
- Then go to #projectPath/target/mvn-repo. There should be a directory structure underneath describing your groupId. Go deeper into that until you reach the maven-metadata - files. Copy all the files and directories in this directory into $HOST by using the replace-option.
- If this is not your first version: Go back to $HOST and edit the maven-metadata.xml - file by adding all versionnumbers you shared in your $HOST in the <versioning> - section. 
It could look like this if you also had an release:
~~~XML
<?xml version="1.0" encoding="UTF-8"?>
<metadata>
  <groupId>com.myName</groupId>
  <artifactId>ProjectName</artifactId>
  <versioning>
    <release>0.1.5</release> 
    <versions>
      <version>0.1.1</version>
      <version>0.1.2</version>
      <version>0.1.3</version>
      <version>0.1.4</version>
      <version>0.1.5</version>
    </versions>
    <lastUpdated>20150713124510</lastUpdated>
  </versioning>
</metadata>
~~~

- Upload the $HOST into github to any branch. Now every other user can integrate your project like i described before.  They have to add

 ~~~XML
 <repository>
    <id>anyID</id>
    <url>https:/github.org/YOUR_ACCOUNT_NAME/YOUR_REPOSITORY_NAME/raw/YOUR_BRANCH_NAME</url>
</repository>
~~~

to the repositories-section. The addition in the Dependencies could look like this:
~~~XML
<dependency>
    <groupId>com.myName</groupId>
    <artifactId>ProjectName</artifactId>
    <version>v0.1.5</version>
    <type>jar</type>
</dependency>
~~~

I hope I could help to make programming easier for you. 


