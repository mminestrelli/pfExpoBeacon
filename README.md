# Beacons App para Expo ITBA

## Instalación

### 1. Instalar Node

#### Opción 1:

```
brew install node
```

Es posible que al instalarlo pida:

```
If you need to have icu4c first in your PATH run:
  echo 'export PATH="/usr/local/opt/icu4c/bin:$PATH"' >> ~/.bash_profile
  echo 'export PATH="/usr/local/opt/icu4c/sbin:$PATH"' >> ~/.bash_profile

For compilers to find icu4c you may need to set:
  export LDFLAGS="-L/usr/local/opt/icu4c/lib"
  export CPPFLAGS="-I/usr/local/opt/icu4c/include"
```

#### Opción 2:

```
npm install -g n.
nvm install node --reinstall-packages-from=node
```

### 2. Correr la app

#### Correr node

```
npm start
```

#### Instalar app en el device

```
cd pfExpoBeacon
react-native run-android
```

A veces, es conveniente instalarla reseteando caches ya que pueden ocurrir errores al cargar js.

```
cd pfExpoBeacon
react-native run-android -- --reset-cache
```



