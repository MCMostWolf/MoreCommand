# Changelog

### Version 1.16.5 - 1.4.1

- remove translation files
    - now displays proper messages when only installed on the server

### Version 1.16.5 - 1.3

- move config to json format to have the same config on forge and fabric
- add command `/healthcommand`
    - `/healthcommand github` shows the url to the github page
    - `/healthcommand discord` shows the url to the discord server
    - `/healthcommand issues` shows the url to the issues page
    - `/healthcommand curseforge` shows the url to the curseforge page
    - `/healthcommand modrinth` shows the url to the modrinth page
    - `/healthcommand config`
        - `/healthcommand config reload` reloads the config
        - `/healthcommand config show` shows the config
        - `/healthcommand config reset` resets the config to its default values

### Version 1.16.5 - 1.2

- move config file to normal forge-config-folder

### Version 1.16.5 - 1.1

- added new subcommand `/health reset <targets>`
    - will reset the maximum health of the entity to the previous base value
    - `/health set <targets> <amount>` will now reduce the maximum health of the targets to the set amount

### Version 1.16.5 - 1.0

- added `/health` command
