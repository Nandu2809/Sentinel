import { Injectable } from '@angular/core';
import { Observable, delay, of } from 'rxjs';
import { Threat } from '../models/security.model';
import { generateThreats } from './mock-data';

@Injectable({ providedIn: 'root' })
export class ThreatService {
  /** GET /api/v1/threats — Threat Detection Engine output */
  getThreats(): Observable<Threat[]> {
    return of(generateThreats()).pipe(delay(300));
  }
}
