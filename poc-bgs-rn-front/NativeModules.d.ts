// NativeModules.d.ts

// Importa os tipos base do React Native
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { NativeModules } from 'react-native';

// Define a "forma" do seu módulo nativo
interface AppLauncherModule {
  // O nome do método tem de ser igual ao do Kotlin
  // Os tipos dos argumentos também (string -> string)
  openApp(packageName: string, activityName: string): void;
}

// Diz ao TypeScript que o objeto NativeModules contém uma chave "AppLauncher"
// que corresponde à interface que acabámos de definir.
declare module 'react-native' {
  interface NativeModulesStatic {
    AppLauncher: AppLauncherModule;
  }
}
