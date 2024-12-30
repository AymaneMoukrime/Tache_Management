import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient } from '@angular/common/http';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { provideToastr } from 'ngx-toastr'; // Import provideToastr
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { importProvidersFrom } from '@angular/core';

// Extend appConfig to include HttpClient and Toastr providers
const extendedAppConfig = {
  ...appConfig,
  providers: [
    ...(appConfig.providers || []), // Retain existing providers from appConfig
    provideHttpClient(), // Add HttpClient provider
    provideToastr({ // Add Toastr configuration
      timeOut: 3000,
      positionClass: 'toast-top-right',
      preventDuplicates: true
    }),
    importProvidersFrom(BrowserAnimationsModule) // Import BrowserAnimationsModule
  ]
};

bootstrapApplication(AppComponent, {
  providers: [
    ...extendedAppConfig.providers
  ]
}).catch((err) => console.error(err));