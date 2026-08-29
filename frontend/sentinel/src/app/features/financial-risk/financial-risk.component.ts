import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

interface FinancialSummary {
  totalEvaluated: number;
  approveCount: number;
  reviewCount: number;
  blockCount: number;
}

interface FinancialDecision {
  id: string;
  userId: string;
  threatId: string;
  score: number;
  riskLevel: string;
  decision: string;
  timestamp: string;
}

@Component({
  selector: 'app-financial-risk',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="p-6 bg-slate-900 text-white min-h-screen">
      <!-- Header -->
      <div class="mb-6 flex justify-between items-center border-b border-slate-800 pb-4">
        <div>
          <h1 class="text-2xl font-bold text-emerald-400 flex items-center gap-2">
            <span class="inline-block w-3 h-3 bg-emerald-500 rounded-full animate-ping"></span>
            Financial Risk & AI Decision Engine Workspace
          </h1>
          <p class="text-slate-400 text-sm">
            Razorpay Track 2 AI Risk Manager — Coordinated Payment Abuse & Cost-Aware Decision Optimization
          </p>
        </div>
        <button
          (click)="loadData()"
          class="px-4 py-2 bg-emerald-600 hover:bg-emerald-500 text-white text-sm font-semibold rounded shadow transition"
        >
          Refresh Live Stream
        </button>
      </div>

      <!-- Overview Metric Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <div class="bg-slate-800 p-4 rounded-lg border border-slate-700">
          <div class="text-xs text-slate-400 font-semibold uppercase">Total Evaluated</div>
          <div class="text-3xl font-extrabold text-white mt-1">{{ summary.totalEvaluated }}</div>
          <div class="text-xs text-slate-400 mt-1">Real-time financial telemetry</div>
        </div>
        <div class="bg-slate-800 p-4 rounded-lg border border-emerald-500/30">
          <div class="text-xs text-emerald-400 font-semibold uppercase">Approved</div>
          <div class="text-3xl font-extrabold text-emerald-400 mt-1">{{ summary.approveCount }}</div>
          <div class="text-xs text-slate-400 mt-1">Low-risk instant authorizations</div>
        </div>
        <div class="bg-slate-800 p-4 rounded-lg border border-amber-500/30">
          <div class="text-xs text-amber-400 font-semibold uppercase">Manual Review</div>
          <div class="text-3xl font-extrabold text-amber-400 mt-1">{{ summary.reviewCount }}</div>
          <div class="text-xs text-slate-400 mt-1">Analyst investigation queue</div>
        </div>
        <div class="bg-slate-800 p-4 rounded-lg border border-rose-500/30">
          <div class="text-xs text-rose-400 font-semibold uppercase">Blocked & Alerted</div>
          <div class="text-3xl font-extrabold text-rose-400 mt-1">{{ summary.blockCount }}</div>
          <div class="text-xs text-slate-400 mt-1">High-risk fraud mitigations</div>
        </div>
      </div>

      <!-- Main Workspace Grid -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Live Decisions Table (2 cols) -->
        <div class="lg:col-span-2 bg-slate-800 p-4 rounded-lg border border-slate-700">
          <h2 class="text-lg font-semibold text-white mb-4 flex items-center justify-between">
            <span>Real-Time Financial Risk Stream</span>
            <span class="text-xs font-normal text-slate-400">Kafka Topic: sentinel.financial.events</span>
          </h2>
          <div class="overflow-x-auto">
            <table class="w-full text-left text-sm text-slate-300">
              <thead class="text-xs uppercase bg-slate-900/60 text-slate-400 border-b border-slate-700">
                <tr>
                  <th class="p-3">Tx ID / Threat ID</th>
                  <th class="p-3">User ID</th>
                  <th class="p-3">Risk Score</th>
                  <th class="p-3">Risk Level</th>
                  <th class="p-3">Decision</th>
                  <th class="p-3">Timestamp</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-700/50">
                <tr *ngFor="let dec of decisions" (click)="selectDecision(dec)" class="hover:bg-slate-700/40 cursor-pointer transition">
                  <td class="p-3 font-mono text-xs text-emerald-300 truncate max-w-[140px]">{{ dec.threatId }}</td>
                  <td class="p-3 font-mono text-xs">{{ dec.userId }}</td>
                  <td class="p-3">
                    <span class="font-bold" [ngClass]="getScoreColor(dec.score)">{{ dec.score }}/100</span>
                  </td>
                  <td class="p-3">
                    <span class="px-2 py-0.5 text-xs rounded font-semibold" [ngClass]="getRiskLevelBadge(dec.riskLevel)">
                      {{ dec.riskLevel }}
                    </span>
                  </td>
                  <td class="p-3">
                    <span class="px-2 py-0.5 text-xs rounded font-bold" [ngClass]="getDecisionBadge(dec.decision)">
                      {{ dec.decision }}
                    </span>
                  </td>
                  <td class="p-3 text-xs text-slate-400">{{ dec.timestamp | date:'shortTime' }}</td>
                </tr>
                <tr *ngIf="decisions.length === 0">
                  <td colspan="6" class="p-6 text-center text-slate-500">No financial risk events recorded yet.</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Risk Intelligence & Scenario Panel (1 col) -->
        <div class="space-y-6">
          <!-- Selected Risk Details -->
          <div class="bg-slate-800 p-4 rounded-lg border border-slate-700">
            <h2 class="text-lg font-semibold text-white mb-3">Decision Intelligence Detail</h2>
            <div *ngIf="selectedDecision; else noSelection" class="space-y-3 text-sm">
              <div class="p-3 bg-slate-900 rounded border border-slate-700">
                <div class="text-xs text-slate-400">Transaction Ref</div>
                <div class="font-mono text-emerald-400 font-semibold truncate">{{ selectedDecision.threatId }}</div>
              </div>
              <div class="flex justify-between items-center p-2 bg-slate-900/50 rounded">
                <span class="text-slate-400">User Account</span>
                <span class="font-mono font-semibold text-white">{{ selectedDecision.userId }}</span>
              </div>
              <div class="flex justify-between items-center p-2 bg-slate-900/50 rounded">
                <span class="text-slate-400">Risk Assessment</span>
                <span class="font-bold" [ngClass]="getScoreColor(selectedDecision.score)">{{ selectedDecision.score }} / 100</span>
              </div>
              <div class="flex justify-between items-center p-2 bg-slate-900/50 rounded">
                <span class="text-slate-400">Action Policy</span>
                <span class="px-2 py-0.5 text-xs rounded font-bold" [ngClass]="getDecisionBadge(selectedDecision.decision)">
                  {{ selectedDecision.decision }}
                </span>
              </div>

              <!-- Topology Signal Indicators -->
              <div class="pt-2 border-t border-slate-700">
                <div class="text-xs font-semibold text-slate-400 mb-2">Relationship Graph Signals</div>
                <div class="flex flex-wrap gap-1">
                  <span class="px-2 py-0.5 bg-slate-700 text-slate-300 text-xs rounded">Device Fingerprint Cluster</span>
                  <span class="px-2 py-0.5 bg-slate-700 text-slate-300 text-xs rounded">IP Subnet Burst</span>
                  <span class="px-2 py-0.5 bg-slate-700 text-slate-300 text-xs rounded">Payment Token Sharing</span>
                </div>
              </div>
            </div>
            <ng-template #noSelection>
              <div class="p-6 text-center text-slate-500 text-sm">Select a transaction event from the table to view risk details.</div>
            </ng-template>
          </div>

          <!-- Hardened Benchmark Scenarios -->
          <div class="bg-slate-800 p-4 rounded-lg border border-slate-700">
            <h2 class="text-lg font-semibold text-white mb-2">Track 2 Benchmark Scenarios</h2>
            <p class="text-xs text-slate-400 mb-3">Hardened synthetic benchmark fraud vs legitimate performance</p>
            <div class="space-y-2 text-xs">
              <div class="flex justify-between p-2 bg-slate-900/60 rounded">
                <span class="text-emerald-300">EVASIVE_FRAUD (Baseline 95% -> Graph 100%)</span>
                <span class="text-emerald-400 font-bold">+4.35% Gain</span>
              </div>
              <div class="flex justify-between p-2 bg-slate-900/60 rounded">
                <span class="text-emerald-300">LEGIT_SHARED_INFRA (FP 6 -> FP 0)</span>
                <span class="text-emerald-400 font-bold">0 False Positives</span>
              </div>
              <div class="flex justify-between p-2 bg-slate-900/60 rounded">
                <span class="text-slate-400">LOW_AND_SLOW_RING</span>
                <span class="text-amber-400 font-bold">Detected</span>
              </div>
              <div class="flex justify-between p-2 bg-slate-900/60 rounded">
                <span class="text-slate-400">COMBINED_RING</span>
                <span class="text-emerald-400 font-bold">100% Catch</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class FinancialRiskComponent implements OnInit {
  summary: FinancialSummary = { totalEvaluated: 0, approveCount: 0, reviewCount: 0, blockCount: 0 };
  decisions: FinancialDecision[] = [];
  selectedDecision: FinancialDecision | null = null;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.http.get<FinancialSummary>('/api/v1/financial-risk/summary').subscribe({
      next: (res) => this.summary = res,
      error: () => console.log('Using default summary')
    });

    this.http.get<FinancialDecision[]>('/api/v1/financial-risk/decisions').subscribe({
      next: (res) => {
        this.decisions = res;
        if (this.decisions.length > 0 && !this.selectedDecision) {
          this.selectedDecision = this.decisions[0];
        }
      },
      error: () => console.log('Using default decisions')
    });
  }

  selectDecision(dec: FinancialDecision): void {
    this.selectedDecision = dec;
  }

  getScoreColor(score: number): string {
    if (score >= 75) return 'text-rose-400';
    if (score >= 45) return 'text-amber-400';
    return 'text-emerald-400';
  }

  getRiskLevelBadge(level: string): string {
    switch (level?.toUpperCase()) {
      case 'CRITICAL': return 'bg-rose-950 text-rose-300 border border-rose-800';
      case 'HIGH': return 'bg-amber-950 text-amber-300 border border-amber-800';
      case 'MEDIUM': return 'bg-yellow-950 text-yellow-300 border border-yellow-800';
      default: return 'bg-emerald-950 text-emerald-300 border border-emerald-800';
    }
  }

  getDecisionBadge(decision: string): string {
    switch (decision?.toUpperCase()) {
      case 'BLOCK':
      case 'BLOCK_AND_ALERT': return 'bg-rose-600 text-white';
      case 'REVIEW':
      case 'INVESTIGATE': return 'bg-amber-600 text-white';
      default: return 'bg-emerald-600 text-white';
    }
  }
}
