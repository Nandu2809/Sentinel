import { Injectable } from '@angular/core';
import { Observable, delay, of } from 'rxjs';
import { UserRiskProfile } from '../models/security.model';
import { generateRiskProfile } from './mock-data';

@Injectable({ providedIn: 'root' })
export class RiskService {
  /** GET /api/v1/risk/:userId — Risk Intelligence Engine output */
  getUserRisk(name?: string): Observable<UserRiskProfile> {
    return of(generateRiskProfile(name)).pipe(delay(300));
  }
}
