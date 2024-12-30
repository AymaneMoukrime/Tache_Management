import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProjectResponse } from '../../models/project/project-response.model';
import { HttpErrorResponse } from '@angular/common/http';


@Injectable({
  providedIn: 'root',
})
export class ProjectService {
  private baseUrl = 'http://localhost:8082/api/project';

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

  // Fetch all projects for the logged-in user
  getProjectsForUser(): Observable<ProjectResponse[]> {
    return this.http.get<ProjectResponse[]>(`http://localhost:8082/api/project/getAllProjectsForUser`, {
      headers: this.getAuthHeaders(),
    });
  }

  // Create a new project
  createProject(project: Partial<ProjectResponse>): Observable<ProjectResponse> {
    const headers = this.getAuthHeaders();
    return this.http.post<ProjectResponse>(`${this.baseUrl}/addProject`, project, { headers });
  }

  // Fetch a project by its ID
  getProjectById(projectId: number): Observable<ProjectResponse> {
    return this.http.get<ProjectResponse>(`${this.baseUrl}/getproject/${projectId}`, {
      headers: this.getAuthHeaders(),
    });
  }

  // Update project details
  updateProject(projectId: number, updatedProject: Partial<ProjectResponse>): Observable<ProjectResponse> {
    return this.http.put<ProjectResponse>(`${this.baseUrl}/updateProject/${projectId}`, updatedProject, {
      headers: this.getAuthHeaders(),
    });
  }

  getUsersByProjectId(projectId: number): Observable<string[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<string[]>(`${this.baseUrl}/UserOfProject/${projectId}`, { headers });
  }
  
  // ProjectService
getProjectUsers(projectId: number): Observable<string[]> {
  const headers = this.getAuthHeaders();
  return this.http.get<string[]>(`${this.baseUrl}/UserOfProject/${projectId}`,  { headers });
}

addUserToProjectByEmail(projectId: number, email: string): Observable<any> {
  const headers = this.getAuthHeaders();
  return this.http.post(
    `${this.baseUrl}/addUserToProjectByemail/${projectId}/user?Email=${email}`,
    null, // Body is null for this POST request
    { headers, responseType: 'text' as 'json' } // Specify responseType here
  );
}


removeUserFromProjectByEmail(projectId: number, email: string): Observable<any> {
  const headers = this.getAuthHeaders();
  return this.http.post(
    `${this.baseUrl}/removeUserFromProject/${projectId}/user?Email=${email}`,
    null, // No request body
    { headers, responseType: 'text' as 'json' } // Expect plain text response
  );
}

// TeamService
createTeam(projectId: number, team: any): Observable<any> {
  const headers = this.getAuthHeaders();
  return this.http.post(`${this.baseUrl}/create_team/${projectId}`, team,  { headers });
}

deleteTeam(projectId: number, teamId: number): Observable<any> {
  const headers = this.getAuthHeaders();
  return this.http.delete(`${this.baseUrl}/delete_team/${projectId}/${teamId}`,  { headers });
}

deleteProject(projectId: number): Observable<any> {
  const headers = this.getAuthHeaders();
  return this.http.delete(`${this.baseUrl}/deleteProject/${projectId}`, {
    headers,
    observe: 'response', // Observe full HTTP response
    responseType: 'text', // Expect plain text response
  });
}


}
