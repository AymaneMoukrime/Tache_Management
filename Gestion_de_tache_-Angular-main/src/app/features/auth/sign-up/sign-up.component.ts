import { Component } from '@angular/core';
import { AuthService } from '../../../services/auth/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-sign-up',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.css'], 
})
export class SignUpComponent {
  user = {
    name: '',
    email: '',
    password: '',
  };

  errorMessage: string | null = null;
  isSubmitting = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  onRegister() {
    if (!this.user.name || !this.user.email || !this.user.password) {
      this.toastr.warning('Veuillez remplir tous les champs.', 'Avertissement');
      return;
    }

    this.isSubmitting = true; // Show the loader
    this.authService.signUp(this.user).subscribe({
      next: (response) => {
        this.isSubmitting = false; // Hide the loader after submission
        this.toastr.success('Compte créé avec succès ! Veuillez vous connecter.', 'Succès');
        this.router.navigate(['/signin']); // Redirect to login page
      },
      error: (error) => {
        this.isSubmitting = false; // Hide the loader on error

        if (error.status === 406 && error.error === 'User already exists') {
          this.errorMessage = 'Cet utilisateur existe déjà. Veuillez vous connecter.';
          this.toastr.info(this.errorMessage, 'Info');
          this.router.navigate(['/signin']); // Redirect to login if the user exists
        } else if (error.status === 403) {
          this.errorMessage = 'Veuillez vérifier les informations fournies.';
          this.toastr.warning(this.errorMessage, 'Avertissement');
        } else if (error.status === 500) {
          this.errorMessage = 'Erreur interne du serveur. Veuillez réessayer plus tard.';
          this.toastr.error(this.errorMessage, 'Erreur du serveur');
        } else {
          this.errorMessage = 'Une erreur inconnue est survenue lors de l’inscription.';
          this.toastr.error(this.errorMessage, 'Erreur');
        }
        console.error('Error:', error);
      },
    });
  }
}
