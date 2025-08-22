/* eslint-disable react-native/no-inline-styles */
import React, {useState} from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  ActivityIndicator,
  Keyboard,
  StyleSheet,
  NativeModules,
  Platform,
} from 'react-native';

const TopBar = ({title}: {title: string}) => {
  return (
    <View style={styles.topBarContainer}>
      <Text style={styles.topBarTitle}>{title}</Text>
    </View>
  );
};

const StatusIndicator = ({isConnected}: {isConnected: boolean}) => {
  const statusText = isConnected
    ? 'Conectado ao Serviço'
    : 'Não conectado ao Serviço';

  return (
    <View style={styles.statusContainer}>
      <View
        style={[
          styles.statusIndicatorDot,
          {backgroundColor: isConnected ? '#22C55E' : '#EF4444'},
        ]}
      />
      <Text style={styles.statusText}>{statusText}</Text>
    </View>
  );
};

export function StartScreen() {
  const {AppLauncher} = NativeModules;
  const [isConnected, setIsConnected] = useState(false);
  const [url, setUrl] = useState('http://10.0.2.16:8080');
  const [isLoading, setIsLoading] = useState(false);

  const handleOpenBackendApp = () => {
    if (Platform.OS === 'android') {
      const packageName = 'com.lbs.background_android_service';
      const activityName = 'com.lbs.background_android_service.MainActivity';
      AppLauncher.openApp(packageName, activityName);
    }
  };

  const handleConnect = async () => {
    if (isLoading) {
      return;
    }
    Keyboard.dismiss();
    setIsLoading(true);
    setIsConnected(false);

    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 5000);
      const response = await fetch(`${url}/status`, {
        signal: controller.signal,
      });
      clearTimeout(timeoutId);

      if (response.ok) {
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
    <View style={styles.screenContainer}>
      <TopBar title="POC BGS RN Front" />

      <View style={styles.contentContainer}>
        <View style={styles.contentWrapper}>
          <StatusIndicator isConnected={isConnected} />

          <TextInput
            style={styles.textInput}
            value={url}
            onChangeText={setUrl}
            placeholder="URL do Serviço Backend"
            autoCapitalize="none"
            keyboardType="url"
            editable={!isLoading}
          />

          <TouchableOpacity
            style={[
              styles.button,
              {backgroundColor: isLoading ? '#9CA3AF' : '#2563EB'},
            ]}
            onPress={handleConnect}
            disabled={isLoading}
            activeOpacity={0.7}>
            {isLoading ? (
              <ActivityIndicator color="#FFFFFF" />
            ) : (
              <Text style={styles.buttonText}>Conectar</Text>
            )}
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.button, styles.openAppButton]}
            onPress={handleOpenBackendApp}
            activeOpacity={0.7}>
            <Text style={styles.buttonText}>Abrir App Backend</Text>
          </TouchableOpacity>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  screenContainer: {
    flex: 1,
    backgroundColor: '#F3F4F6',
  },
  topBarContainer: {
    backgroundColor: '#166534',
    height: 56,
    justifyContent: 'center',
    alignItems: 'center',
    elevation: 4,
    shadowOpacity: 0.2,
    shadowRadius: 2,
    shadowOffset: {height: 2, width: 0},
  },
  topBarTitle: {
    color: '#FFFFFF',
    fontSize: 20,
    fontWeight: 'bold',
  },
  contentContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
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
    color: '#374151',
  },
  contentWrapper: {
    width: '100%',
    maxWidth: 384,
  },
  textInput: {
    width: '100%',
    height: 48,
    borderWidth: 1,
    borderColor: '#D1D5DB',
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
  openAppButton: {
    backgroundColor: '#16A34A',
    marginTop: 12,
  },
});
