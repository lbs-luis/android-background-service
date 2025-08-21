#!/bin/bash

# Caminho absoluto do adb
ADB="$HOME/.android/Sdk/platform-tools/adb"

# Porta local que deseja remover
LOCAL_PORT=8080

# Remove o forward específico
$ADB forward --remove tcp:$LOCAL_PORT

if [ $? -eq 0 ]; then
    echo "Forward da porta $LOCAL_PORT removido com sucesso."
else
    echo "Erro ao remover o forward."
fi

