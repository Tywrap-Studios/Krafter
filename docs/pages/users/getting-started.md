# Getting Started
## Prerequisites
Before you can run Krafter on your device, you need to have the following available:

### Docker
The service is able to run in a Docker Container and this is heavily suggested too. While you can
technically run the raw file standalone, using Docker ensures that the environment the 
bot works in is trusted, safe and tested.

::: warning
Do not expect support with the software if you are running it in untested environments!
:::

For info on how to install and use Docker, view the [official documentation](https://docs.docker.com/get-started/introduction/).
Using Docker Compose is also possible, if you please so.

### The distribution
Krafter is currently not available as a pullable image from a registry, you will have to build
it yourself. We've tried to make this process as simple as possible, but first, you will need
the required distributions and a `Dockerfile`.
1. Get the `.tar` distribution from the [official releases page](https://github.com/Tywrap-Studios/Krafter/releases/latest).
2. Get the `Dockerfile` from the [same page](https://github.com/Tywrap-Studios/Krafter/releases/latest).
3. Transfer these files to the device/server you want to run the software on, and ensure the following file structure:
```
\build
 \distributions
  \Krafter-<version>.tar
Dockerfile
```

## Building the image
You can build the image from the provided `Dockerfile`.
### Run through
We understand building random `Dockerfile`s feels like a security risk, especially if you
don't understand what they do or what the results may be. Therefore, let's take a look at
what happens in ours:
```dockerfile
FROM eclipse-temurin:21-jdk-jammy
```
First, it extends the `eclipse-temurin:21-jdk-jammy` image from Docker Hub.
This image provides a JRE (version 21) for the bot to run in, alongside an Ubuntu environment
for this JRE. This image is based on the JDKs by Eclipse Temurin.
```dockerfile
# Create required directories
RUN mkdir -p /bot/plugins
RUN mkdir -p /bot/data
RUN mkdir -p /bot/config
RUN mkdir -p /dist/out

# Declare required volumes
VOLUME [ "/bot/data" ]
VOLUME [ "/bot/config" ]
VOLUME [ "/bot/plugins" ]
```
Then, it makes new directories and declares volumes (that persist on the host disk, rather than in the container) for most of these.
In the `/bot/` directories, the bot stores its important information. The `/dist/out` directory
is where the extracted distribution will be stored to run the software.
```dockerfile
# Copy the distribution files into the container
COPY [ "build/distributions/Krafter-<version>.tar", "/dist" ]

# Extract the distribution files, and prepare them for use
RUN tar -xf /dist/Krafter-<version>.tar -C /dist/out
RUN chmod +x /dist/out/Krafter-<version>/bin/Krafter

# Clean up unnecessary files
RUN rm /dist/Krafter-<version>.tar
```
In this step we copy over the downloaded `.tar` distribution and move it to `/dist`,
then, we extract them and make the extracted binary (`/bin/Krafter`) available through `chmod`.
Afterwards it deletes the copied over `.tar` distribution.
```dockerfile
# Set the correct working directory
WORKDIR /bot

# Run the distribution start script
ENTRYPOINT [ "/dist/out/Krafter-<version>/bin/Krafter" ]
```
Finally, we set the working directory of the runtime to `/bot`, and declare the extracted
binary as the entrypoint for our container, aka, what will be run when the container
is started.

### Building the image
You can then build the image as usual:
```bash
$ docker build ./ -t tag
```
::: info NOTE
On some implementations of Docker, the `./` value is not needed.
:::

## Running the image
After you've successfully built your image, you can run the following command to
initiate a container and run the bot. It is suggested to run this container once, and then 
exit it to [configure your bot](../config).
```bash
$ docker run --env=TOKEN=<token> --env=DISCORD_LOGGER_URL=<webhook> -d <tag>
```
> [!IMPORTANT]
> It's very important to add these `--env` variables when you run the `run` command. At the moment, Docker
> does not let you modify these env values after the container has been made.  
> 
> For the seasoned user: you may use Secrets if you're able to, but you will still need to use
> these `--env` options to point to them.  
> 
> Alternatively, Docker Compose does let you define environment values apart from the `run` command.
> While we still suggest using this approach as it is tested, if using that works for you,
> feel free to.

## Finalising
Ensure your bot is online by checking it in Discord, if it displays the following status,
it is an indicator that your bot ran according to the software and also successfully
read the default config values.  
````
Playing with Cords
````

## Continue
- By learning how to configure your bot [here](../config)
- And check out the different modules in the sidebar
- By checking out how to use [PluralKit](./pluralkit)
