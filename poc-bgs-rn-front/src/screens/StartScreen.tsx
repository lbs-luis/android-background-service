/* eslint-disable react-native/no-inline-styles */
import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  ActivityIndicator,
  Keyboard,
  StyleSheet,
} from 'react-native';

// Componente para o indicador de status (bolinha e texto)
const StatusIndicator = ({ isConnected }: { isConnected: boolean }) => {
  const statusText = isConnected ? 'Conectado ao Serviço' : 'Não conectado ao Serviço';

  return (
    <View style={styles.statusContainer}>
      <View
        style={[
          styles.statusIndicatorDot,
          { backgroundColor: isConnected ? '#22C55E' : '#EF4444' }, // green-500 or red-500
        ]}
      />
      <Text style={styles.statusText}>{statusText}</Text>
    </View>
  );
};

export function StartScreen() {
  const [isConnected, setIsConnected] = useState(false);
  // Usando o IP padrão para o host a partir do emulador
  const [url, setUrl] = useState('http://10.0.2.16:8080');
  const [isLoading, setIsLoading] = useState(false);

  const handleConnect = async () => {
    if (isLoading) {return;}
    Keyboard.dismiss(); // Esconde o teclado
    setIsLoading(true);
    setIsConnected(false); // Reseta o status a cada nova tentativa

    try {
      // O AbortController cancela a requisição se ela demorar muito (ex: 5 segundos)
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 5000);

      const response = await fetch(`${url}/status`, { signal: controller.signal });

      clearTimeout(timeoutId); // Limpa o timeout se a resposta chegar a tempo

      if (response.ok) { // Status 200-299
        setIsConnected(true);
      } else {
        console.log(`Servidor respondeu com status: ${response.status}`);
      }
    } catch (error) {
      console.error('Falha na conexão:', error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.contentWrapper}>
        <StatusIndicator isConnected={isConnected} />

        <TextInput
          style={styles.textInput}
          value={url}
          onChangeText={setUrl}
          placeholder="URL do Serviço Backend"
          autoCapitalize="none"
          keyboardType="url"
          editable={!isLoading} // Não permite editar enquanto carrega
        />

        <TouchableOpacity
          style={[
            styles.button,
            { backgroundColor: isLoading ? '#9CA3AF' : '#2563EB' }, // gray-400 or blue-600
          ]}
          onPress={handleConnect}
          disabled={isLoading}
          activeOpacity={0.7}
        >
          {isLoading ? (
            <ActivityIndicator color="#FFFFFF" />
          ) : (
            <Text style={styles.buttonText}>Conectar</Text>
          )}
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  // Estilos para StatusIndicator
  statusContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 24,
    alignSelf: 'flex-start',
  },
  statusIndicatorDot: {
    width: 16,
    height: 16,
    borderRadius: 8,
  },
  statusText: {
    marginLeft: 12,
    fontSize: 18,
    color: '#374151', // gray-700
  },
  // Estilos para StartScreen
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F3F4F6', // gray-100
    padding: 20,
  },
  contentWrapper: {
    width: '100%',
    maxWidth: 384,
  },
  textInput: {
    width: '100%',
    height: 48,
    borderWidth: 1,
    borderColor: '#D1D5DB', // gray-300
    borderRadius: 8,
    paddingHorizontal: 16,
    marginBottom: 16,
    fontSize: 16,
    backgroundColor: '#FFFFFF',
  },
  button: {
    width: '100%',
    height: 48,
    borderRadius: 8,
    justifyContent: 'center',
    alignItems: 'center',
  },
  buttonText: {
    color: '#FFFFFF',
    fontSize: 18,
    fontWeight: 'bold',
  },
});
