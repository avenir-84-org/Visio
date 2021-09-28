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

- prevent login for that user: add these line at the end of `/etc/ssh/sshd_config`:
```
Match   User    a84
        PasswordAuthentication  no
```

