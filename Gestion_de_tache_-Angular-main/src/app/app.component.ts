import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';  // Import RouterModule
import { SignInComponent } from './features/auth/sign-in/sign-in.component';
import { SignUpComponent } from './features/auth/sign-up/sign-up.component';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { HttpClientModule } from '@angular/common/http';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [HttpClientModule, RouterModule ,RouterOutlet],  // Import RouterModule
  template: `
    <router-outlet>  </router-outlet>  <!-- The components will be loaded here -->
  `,
})
export class AppComponent {
  title = 'gestion-taches';
}
