import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TeamDto } from '../../models/team/teamDto.model';


@Injectable({
  providedIn: 'root',
})
export class TeamService {
  private apiUrl = 'http://localhost:8082/api/team';  // Update with the correct API URL

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
  // Fetch teams for a specific project
  getTeamsForProject(projectId: number): Observable<TeamDto[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<TeamDto[]>(`${this.apiUrl}/teams/${projectId}`, { headers , responseType: 'json' });
  }

  getUserTeams () : Observable<TeamDto[]>{
    const headers = this.getAuthHeaders();
    return this.http.get<TeamDto[]>(`${this.apiUrl}/teams`, { headers });
  }

  // Create a new team for a specific project
  createTeam(projectId: number , teamDto : TeamDto ): Observable<TeamDto> {
    const headers = this.getAuthHeaders();
    return this.http.post<TeamDto>(`${this.apiUrl}/create_team/${projectId}`,teamDto,{ headers , responseType: 'json'  });
  }

   

getTeamById (projectId : number , teamId : number):Observable<TeamDto>{
  const headers = this.getAuthHeaders();
  return this.http.get<TeamDto>(`${this.apiUrl}/teamid/${projectId}/${teamId}`,{ headers , responseType: 'json' });
}

  
    // Add a user to a team
    addUserToTeam(projectId: number, teamId: number, userEmail: string): Observable<TeamDto> {
      const headers = this.getAuthHeaders();
      return this.http.post<TeamDto>(`${this.apiUrl}/asignUsertoTeam/${projectId}/${teamId}?Email=${encodeURIComponent(userEmail)}`, {}, { headers , responseType: 'json' });
    }
    
    
  
    // Remove a user from a team
    removeUserFromTeam(projectId: number, teamId: number, userEmail: string): Observable<TeamDto> {
      const headers = this.getAuthHeaders();
      return this.http.post<TeamDto>(`${this.apiUrl}/removeUserFromTeam/${projectId}/${teamId}?Email=${encodeURIComponent(userEmail)}`, {}, { headers , responseType: 'json' });
    }
    

    deleteTeam(projectId: number, teamId: number): Observable<any> {
      const headers = this.getAuthHeaders();

      return this.http.delete(`${this.apiUrl}/delete_team/${projectId}/${teamId}`,{ headers ,  responseType: 'text' });
    }
    
}
