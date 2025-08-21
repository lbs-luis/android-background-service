#!/bin/bash

# Caminho absoluto do adb
ADB="$HOME/.android/Sdk/platform-tools/adb"

# Porta local e do dispositivo
LOCAL_PORT=8080
DEVICE_PORT=8080

# Cria o forward
$ADB forward tcp:$LOCAL_PORT tcp:$DEVICE_PORT

if [ $? -eq 0 ]; then
    echo "Forward de porta $LOCAL_PORT -> $DEVICE_PORT criado com sucesso."
else
    echo "Erro ao criar o forward."
fi

