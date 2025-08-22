/* eslint-disable react-native/no-inline-styles */
import React from 'react';
import { SafeAreaView, StatusBar, useColorScheme } from 'react-native';
import { StartScreen } from './src/screens/StartScreen';

function App(): React.JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';
  const backgroundColor = isDarkMode ? '#121212' : '#F5F5F5';

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor }}>
      <StatusBar
        barStyle={isDarkMode ? 'light-content' : 'dark-content'}
        backgroundColor={backgroundColor}
      />
      <StartScreen />
    </SafeAreaView>
  );
}

export default App;
