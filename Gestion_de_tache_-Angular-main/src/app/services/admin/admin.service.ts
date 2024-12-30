import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {UserResponse} from '../../models/user/user-response.model'

// Interface for UserResponse

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private baseUrl = 'http://localhost:8082/api/admin'; // Backend API base URL

  constructor(private http: HttpClient) {}

  /**
   * Retrieves the Authorization headers safely.
   */
  private getAuthHeaders(): HttpHeaders {
    let token = '';
    if (typeof window !== 'undefined') {
      // Guard for SSR: Ensure code runs in the browser
      token = localStorage.getItem('token') || '';
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    });
    
    return headers;
  }

  /**
   * Fetch all users.
   */
  getAllUsers(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${this.baseUrl}/users`, {
      headers: this.getAuthHeaders(),
    }).pipe(
      map(users =>
        users.map(user => ({
          ...user,
          dateInscription: new Date(user.dateInscription) // Ensure proper Date conversion
        }))
      )
    );
  }

  deleteUserByEmail(email: string): Observable<string> {
    return this.http.delete(`${this.baseUrl}/user/${email}`, {
      headers: this.getAuthHeaders(),
      responseType: 'text', // Explicitly expect a text response
    });
  }

  //update user role
  updateUserRole(email: string, role: string): Observable<any> {
    const url = `${this.baseUrl}/user/${email}/role`;
    const params = { role };
  
    return this.http.post<string>(url, null, {
      params: params,
      headers: this.getAuthHeaders(),
      responseType: 'text' as 'json', // Spécifie que la réponse est texte ou JSON
    });
  }
  
  
  
  
  
  
  
}
