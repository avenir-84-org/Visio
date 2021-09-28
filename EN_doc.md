# Install

## install JITSI MEET

On a Ubuntu 20.04 LTS server base, follow [these instructions](https://www.digitalocean.com/community/tutorials/how-to-install-jitsi-meet-on-ubuntu-20-04).

## create utility user to run prosody commands

- name: a84
- password: choose

```bash
sudo adduser a84
sudo adduser a84 sudo
```

- prevent login for that user: add these lines at the end of `/etc/ssh/sshd_config`:
```
Match   User    a84
        PasswordAuthentication  no
```


# Run

Before running the application, you need to, well, get it.

Use git to recover the code on your server:

```
sudo apt instal git
git clone git@github.com:avenir-84-org/Visio.git
```

This will create a subfolder `Visio/` in the current directory.
You need to then move your shell to that directory.
```
cd Visio
```
Before running the app **the first time only** you need to populate the database using the initialization SQL script from the repo's root directory.
```
sudo mysql -p < init.sql
```

Once that is done, you can start the application and should not encounter any error on startup.
```
./mvnw spring-boot:run
```

# Run the application as a background detached process

The problem with the previous run is that if your connection to the server is lost, the application dies.

Obviously, this is not what we want.

The application should be able to keep running even if you're not there to maintain the connection.

For this, you need to start the application as a background process using `tmux`.

Run the command: `tmux`

This will change your shell so that it looks different and has green borders.
Inside the tmux shell, run the same command:
```
./mvnw spring-boot:run
```
You can now close the terminal, and when you connect again to the server, in order to recover the `tmux` window, run the command:
```
tmux a
```