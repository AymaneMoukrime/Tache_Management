import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SignInComponent } from './sign-in/sign-in.component';
import { SignUpComponent } from './sign-up/sign-up.component';
import { AuthRoutingModule } from './auth-routhing.module';
import { ToastrModule } from 'ngx-toastr';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@NgModule({
  declarations: [],
  imports: [CommonModule, AuthRoutingModule , FormsModule , SignInComponent, SignUpComponent, BrowserAnimationsModule ] ,
  exports: [SignInComponent, SignUpComponent], // Optional: export components if needed
})
export class AuthModule {}
