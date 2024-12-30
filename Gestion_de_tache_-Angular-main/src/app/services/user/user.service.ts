import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProjectResponse } from '../../models/project/project-response.model';

export interface UserResponse {
  id: number;
  name: string;
  email: string;
  dateInscription: string;
  userRole: string;
}

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private baseUrl = 'http://localhost:8082/api/user'; // Backend API base URL

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
    console.log('Authorization Token:', token);

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    });
    
    return headers;
  }

  /**
   * Fetch all users with authentication.
   */
  getAllUsers(): Observable<UserResponse[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<UserResponse[]>(`${this.baseUrl}/users`, { headers });
  }

  /**
   * Search for users by name with authentication.
   * @param name - The name to search for
   */
  searchUsersByName(name: string): Observable<UserResponse[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<UserResponse[]>(`${this.baseUrl}/Userbyname/${name}`, { headers });
  }

  /**
   * Fetch all user emails with authentication.
   */
  getAllUserEmails(): Observable<string[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<string[]>(`${this.baseUrl}/UsersMail`, { headers });
  }

  /**
   * Delete a user by email with authentication.
   * @param email - User's email to delete
   */
  deleteUser(email: string): Observable<string> {
    const headers = this.getAuthHeaders();
    return this.http.delete<string>(`${this.baseUrl}/delete/${email}`, { headers });
  }

  /**
   * Update a user's role with authentication.
   * @param email - User's email
   * @param role - New role for the user
   */
  updateUserRole(email: string, role: string): Observable<string> {
    const headers = this.getAuthHeaders();
    const body = { role }; // Role in the request body
    return this.http.post<string>(`${this.baseUrl}/role/${email}`, body, { headers });
  }

  
  getProjectsForUser(): Observable<ProjectResponse[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<ProjectResponse[]>(`http://localhost:8082/api/project/getAllProjectsForUser`, { headers });
  }
  
 updateUserInfo(user: { name: string; email: string }): Observable<any> {
    return this.http.post(`${this.baseUrl}/UpdateUser`, null, {
      params: {
        name: user.name,
        email: user.email,
      },
    });
  }
  getUserDetails(): Observable<any> {
    return this.http.get(`${this.baseUrl}/users`);
  }

  

  
}
