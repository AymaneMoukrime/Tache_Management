import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JwtHelperService } from '@auth0/angular-jwt';
import {jwtDecode} from 'jwt-decode';


export interface SignupResponse {
  message: string;
  // Add other properties if necessary
}

export interface AuthResponse {
  jwt: string;       // JWT token
  userId: number;    // User ID
  userRole: string;  // User Role
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private baseUrl = 'http://localhost:8082/api/auth'; // Backend URL
  private jwtHelper = new JwtHelperService();

  constructor(private http: HttpClient) {}

  private getToken(): string | null {
    return localStorage.getItem('token');
  }

  /** 
   * Signup method 
   */
  signUp(user: any): Observable<SignupResponse> {
    return this.http.post<SignupResponse>(`${this.baseUrl}/signup`, user);
  }

  /** 
   * Signin method 
   */
  signIn(authRequest: { email: string; password: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, authRequest);
  }

   // Check if the user is authenticated by checking for the token in localStorage
   isAuthenticated(): boolean {
    const token = this.getToken();
    return token !== null;  // Check if there is a token in localStorage
  }

  // Optionally, create a method to log the user out
  logout(): void {
    localStorage.removeItem('token');  // Remove the token from localStorage
  }

  /** 
   * Decode JWT token and extract the user role
   */
  getDecodedRole(token: string | null): string | null {
    if (!token) {
      console.error('Token is null or undefined.');
      return null;
    }
  
    try {
      const decodedToken = this.jwtHelper.decodeToken(token);
  
      // Debugging decoded token structure
      console.log('Decoded Token:', decodedToken);
  
      // Extract the roles from the decoded token
      const rolesArray = decodedToken?.roles || [];
      if (Array.isArray(rolesArray) && rolesArray.length > 0) {
        const role = rolesArray[0]; // Access the first role in the array
        console.log('Extracted Role:', role);
        return role;
      } else {
        console.warn('No roles found in token.');
        return null;
      }
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  }

  getUserIdFromToken(): number | null {
    const token = localStorage.getItem('token'); // Ensure the token is stored
    if (!token) {
      console.error('Token not found'); // Debugging step
      return null;
    }
  
    try {
      const decodedToken: any = jwtDecode(token);
      console.log('Decoded Token:', decodedToken); // Debugging step
      return decodedToken.id || null; // Replace 'userId' with the actual key if different
    } catch (error) {
      console.error('Error decoding token:', error); // Debugging step
      return null;
    }
  }

  getUserEmailFromToken(): string | null {
    const token = localStorage.getItem('token');
    if (!token) {
      return null;
    }
    try {
      const decodedToken: any = jwtDecode(token);
      return decodedToken.sub || null; // Assuming the email is stored in `sub`
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  }
  
  
}
  

