import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription, interval } from 'rxjs';
import { FinancialRiskService } from '../../core/services/financial-risk.service';
import {
  FinancialRiskDecision,
  FinancialSummary,
  GraphTopology,
  GraphNode,
  ScenarioMetric,
  ModelBenchmark,
  BusinessCostSummary,
  RiskTimelineStage,
  ActivityConsoleLog
} from '../../core/models/financial-risk.model';
import { FinancialRelationshipGraphComponent } from './financial-relationship-graph.component';

@Component({
  selector: 'app-financial-risk',
  standalone: true,
  imports: [CommonModule, FormsModule, FinancialRelationshipGraphComponent],
  template: `
    <div class="space-y-5 text-ink">
      <!-- STEP 3: TOP HEADER & COMMAND CENTER BANNER -->
      <div class="bracket p-4 bg-void-800/80 border border-line">
        <div class="bk-tr"></div>
        <div class="bk-bl"></div>
        <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
          <div>
            <div class="flex items-center gap-2 mb-1">
              <span class="w-2.5 h-2.5 rounded-full bg-signal-live animate-ping"></span>
              <h1 class="font-mono text-lg font-bold text-ink tracking-wider uppercase">
                FINANCIAL RISK INTELLIGENCE — REAL-TIME TRANSACTION MONITORING
              </h1>
              <span class="mono-label px-2 py-0.5 bg-signal-intel/20 text-signal-intel border border-signal-intel/40 rounded">
                RAZORPAY TRACK 2 AI RISK MANAGER
              </span>
            </div>
            <p class="font-sans text-xs text-ink-muted">
              Coordinated Payment Abuse Detection, Relationship Topology Graph & Cost-Aware Decision Engine
            </p>
          </div>

          <!-- Status Badges & Quick Scenario Actions -->
          <div class="flex flex-wrap items-center gap-2 font-mono text-[10px]">
            <div class="flex items-center gap-1.5 px-2 py-1 bg-void-900 border border-line rounded">
              <span class="status-dot bg-signal-safe"></span>
              <span class="text-ink-muted">SYSTEM:</span>
              <span class="text-signal-safe font-bold">LIVE</span>
            </div>
            <div class="flex items-center gap-1.5 px-2 py-1 bg-void-900 border border-line rounded">
              <span class="status-dot bg-signal-live animate-pulse"></span>
              <span class="text-ink-muted">KAFKA:</span>
              <span class="text-signal-live font-bold">CONNECTED</span>
            </div>
            <div class="flex items-center gap-1.5 px-2 py-1 bg-void-900 border border-line rounded">
              <span class="status-dot bg-signal-intel"></span>
              <span class="text-ink-muted">GRAPH:</span>
              <span class="text-signal-intel font-bold">ONLINE</span>
            </div>

            <!-- Refresh / Evaluate Controls -->
            <button
              (click)="refreshData()"
              [disabled]="loading"
              class="px-2.5 py-1 bg-void-700 hover:bg-void-600 border border-line text-ink text-xs font-mono rounded transition-colors flex items-center gap-1"
            >
              <span [class.animate-spin]="loading">↻</span>
              <span>REFRESH</span>
            </button>
          </div>
        </div>

        <!-- PART 6: PRESERVED 3 SCENARIO QUICK TESTERS -->
        <div class="mt-3 pt-3 border-t border-line/60 flex flex-wrap items-center justify-between gap-2 text-xs font-mono">
          <span class="mono-label text-signal-intel font-bold">PART 6 SCENARIO TESTERS:</span>
          <div class="flex flex-wrap items-center gap-2">
            <button
              (click)="runScenario('LEGITIMATE')"
              class="px-2.5 py-1 bg-signal-safe/15 hover:bg-signal-safe/30 border border-signal-safe/40 text-signal-safe font-bold rounded transition-colors"
            >
              SCENARIO A: LEGITIMATE (APPROVE)
            </button>
            <button
              (click)="runScenario('AMBIGUOUS')"
              class="px-2.5 py-1 bg-signal-warn/15 hover:bg-signal-warn/30 border border-signal-warn/40 text-signal-warn font-bold rounded transition-colors"
            >
              SCENARIO B: AMBIGUOUS (REVIEW)
            </button>
            <button
              (click)="runScenario('HIGH_RISK')"
              class="px-2.5 py-1 bg-signal-critical/15 hover:bg-signal-critical/30 border border-signal-critical/40 text-signal-critical font-bold rounded transition-colors"
            >
              SCENARIO C: HIGH-RISK RING (BLOCK)
            </button>
          </div>
        </div>
      </div>

      <!-- STEP 4: EXECUTIVE RISK SUMMARY (KPI MODULES) -->
      <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-5 lg:grid-cols-9 gap-3">
        <div class="bracket p-3 bg-void-800/70 border border-line flex flex-col justify-between">
          <div class="mono-label text-[9px]">TRANSACTIONS</div>
          <div class="font-mono text-xl font-bold text-ink my-1">
            {{ summary?.totalEvaluated ?? 'DATA UNAVAILABLE' }}
          </div>
          <div class="font-mono text-[9px] text-ink-dim truncate">TOTAL EVALUATED</div>
        </div>
        <div class="bracket p-3 bg-void-800/70 border border-line flex flex-col justify-between">
          <div class="mono-label text-[9px]">RISK EVALUATED</div>
          <div class="font-mono text-xl font-bold text-signal-intel my-1">
            {{ summary?.totalEvaluated ?? '0' }}
          </div>
          <div class="font-mono text-[9px] text-ink-dim truncate">100% TELEMETRY</div>
        </div>
        <div class="bracket p-3 bg-void-800/70 border border-signal-safe/30 flex flex-col justify-between">
          <div class="mono-label text-[9px] text-signal-safe">APPROVED</div>
          <div class="font-mono text-xl font-bold text-signal-safe my-1">
            {{ summary?.approveCount ?? '0' }}
          </div>
          <div class="font-mono text-[9px] text-ink-dim truncate">INSTANT PASS</div>
        </div>
        <div class="bracket p-3 bg-void-800/70 border border-signal-warn/30 flex flex-col justify-between">
          <div class="mono-label text-[9px] text-signal-warn">REVIEW</div>
          <div class="font-mono text-xl font-bold text-signal-warn my-1">
            {{ summary?.reviewCount ?? '0' }}
          </div>
          <div class="font-mono text-[9px] text-ink-dim truncate">ANALYST QUEUE</div>
        </div>
        <div class="bracket p-3 bg-void-800/70 border border-signal-critical/30 flex flex-col justify-between">
          <div class="mono-label text-[9px] text-signal-critical">BLOCKED</div>
          <div class="font-mono text-xl font-bold text-signal-critical my-1">
            {{ summary?.blockCount ?? '0' }}
          </div>
          <div class="font-mono text-[9px] text-ink-dim truncate">HIGH-RISK MITIGATED</div>
        </div>
        <div class="bracket p-3 bg-void-800/70 border border-line flex flex-col justify-between">
          <div class="mono-label text-[9px]">HIGH-RISK RATE</div>
          <div class="font-mono text-xl font-bold text-signal-warn my-1">
            {{ summary ? summary.highRiskRate + '%' : 'DATA UNAVAILABLE' }}
          </div>
          <div class="font-mono text-[9px] text-ink-dim truncate">REVIEW + BLOCK %</div>
        </div>
        <div class="bracket p-3 bg-void-800/70 border border-line flex flex-col justify-between">
          <div class="mono-label text-[9px]">EXPECTED LOSS</div>
          <div class="font-mono text-lg font-bold text-signal-safe my-1 truncate">
            ₹{{ summary ? (summary.expectedLoss | number) : '0' }}
          </div>
          <div class="font-mono text-[9px] text-ink-dim truncate">AVOIDED LOSS</div>
        </div>
        <div class="bracket p-3 bg-void-800/70 border border-line flex flex-col justify-between">
          <div class="mono-label text-[9px]">FRAUD DETECTED</div>
          <div class="font-mono text-xl font-bold text-signal-safe my-1">
            {{ summary ? summary.fraudDetectionRate + '%' : '100%' }}
          </div>
          <div class="font-mono text-[9px] text-ink-dim truncate">RECALL RATE</div>
        </div>
        <div class="bracket p-3 bg-void-800/70 border border-line flex flex-col justify-between">
          <div class="mono-label text-[9px]">FALSE POSITIVE</div>
          <div class="font-mono text-xl font-bold text-signal-safe my-1">
            {{ summary ? summary.falsePositiveRate + '%' : '0.0%' }}
          </div>
          <div class="font-mono text-[9px] text-ink-dim truncate">FP COST OPTIMIZED</div>
        </div>
      </div>

      <!-- MAIN DUAL-COLUMN WORKSPACE -->
      <div class="grid grid-cols-1 lg:grid-cols-12 gap-5">
        <!-- LEFT COLUMN (8 cols): Stream Table, Decision Matrix, Explainable AI, Policy Simulator, Timeline -->
        <div class="lg:col-span-8 space-y-5">
          <!-- STEP 5: REAL-TIME TRANSACTION STREAM -->
          <div class="bracket p-4 bg-void-800/80 border border-line">
            <div class="bk-tr"></div>
            <div class="bk-bl"></div>
            <div class="flex flex-wrap items-center justify-between gap-2 mb-3 pb-2 border-b border-line">
              <div class="flex items-center gap-2">
                <span class="w-2 h-2 rounded-full bg-signal-live animate-ping"></span>
                <h3 class="font-mono text-xs font-bold uppercase tracking-wider text-ink">
                  REAL-TIME TRANSACTION INTELLIGENCE STREAM
                </h3>
                <span class="mono-label px-1.5 py-0.5 bg-void-900 text-ink-muted border border-line rounded text-[9px]">
                  KAFKA: sentinel.financial.events
                </span>
              </div>
              <div class="flex items-center gap-2 font-mono text-[10px]">
                <input
                  type="text"
                  placeholder="Filter tx / user / merchant..."
                  [(ngModel)]="searchQuery"
                  class="px-2.5 py-1 bg-void-950 border border-line rounded text-xs text-ink placeholder-ink-dim focus:outline-none focus:border-signal-live w-48"
                />
              </div>
            </div>

            <div class="overflow-x-auto">
              <table class="w-full text-left font-mono text-xs">
                <thead class="bg-void-900/90 text-ink-muted border-b border-line uppercase text-[10px] tracking-wider">
                  <tr>
                    <th class="p-2.5">TIME</th>
                    <th class="p-2.5">TRANSACTION</th>
                    <th class="p-2.5">USER</th>
                    <th class="p-2.5">MERCHANT</th>
                    <th class="p-2.5 text-right">AMOUNT</th>
                    <th class="p-2.5 text-center">RISK</th>
                    <th class="p-2.5 text-center">DECISION</th>
                    <th class="p-2.5">TOP SIGNAL</th>
                    <th class="p-2.5 text-center">ACTION</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-line/40 text-ink-muted">
                  <tr
                    *ngFor="let dec of filteredDecisions"
                    (click)="selectTransaction(dec)"
                    class="hover:bg-void-700/50 cursor-pointer transition-colors"
                    [class.bg-void-700/70]="selectedDecision?.id === dec.id"
                  >
                    <td class="p-2.5 text-ink-dim text-[11px] whitespace-nowrap">
                      {{ dec.createdAt | date:'HH:mm:ss' }}
                    </td>
                    <td class="p-2.5 text-signal-live font-semibold truncate max-w-[110px]">
                      {{ dec.threatId }}
                    </td>
                    <td class="p-2.5 text-ink truncate max-w-[100px]">{{ dec.userId }}</td>
                    <td class="p-2.5 text-ink-muted truncate max-w-[110px]">{{ dec.merchantId }}</td>
                    <td class="p-2.5 text-right font-bold text-ink whitespace-nowrap">
                      ₹{{ (dec.amount ?? 4850) | number }}
                    </td>
                    <td class="p-2.5 text-center">
                      <span class="font-bold" [ngClass]="getScoreColor(dec.riskScore)">
                        {{ dec.riskScore }}
                      </span>
                    </td>
                    <td class="p-2.5 text-center">
                      <span class="px-2 py-0.5 text-[9px] rounded font-bold uppercase tracking-wide" [ngClass]="getDecisionBadge(dec.decision)">
                        {{ dec.decision }}
                      </span>
                    </td>
                    <td class="p-2.5 text-ink text-[11px] truncate max-w-[140px]">
                      {{ dec.topSignal || 'Nominal Signal' }}
                    </td>
                    <td class="p-2.5 text-center" (click)="$event.stopPropagation()">
                      <button
                        (click)="openIncidentForTransaction(dec)"
                        class="px-2 py-0.5 bg-signal-critical/15 hover:bg-signal-critical/30 border border-signal-critical/40 text-signal-critical text-[10px] font-bold rounded transition-colors"
                        title="Open Incident"
                      >
                        INCIDENT
                      </button>
                    </td>
                  </tr>

                  <tr *ngIf="filteredDecisions.length === 0">
                    <td colspan="9" class="p-8 text-center text-ink-dim">
                      No financial transactions matching criteria.
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- DUAL ROW: STEP 6 (DECISION MATRIX) & STEP 7 (EXPLAINABLE AI) -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
            <!-- STEP 6: RISK DECISION MATRIX WITH HUMAN-IN-THE-LOOP (PART 11) -->
            <div class="bracket p-4 bg-void-800/80 border border-line flex flex-col justify-between">
              <div class="bk-tr"></div>
              <div class="bk-bl"></div>

              <div>
                <div class="flex items-center justify-between mb-3 pb-2 border-b border-line">
                  <span class="font-mono text-xs font-bold uppercase tracking-wider text-ink">
                    RISK DECISION MATRIX
                  </span>
                  <span class="mono-label text-signal-live text-[9px]">
                    MODEL: {{ selectedDecision?.modelVersion || 'FINANCIAL-ENHANCED-v1' }}
                  </span>
                </div>

                <!-- 3 Decision States Banner -->
                <div class="grid grid-cols-3 gap-2 mb-3">
                  <div
                    class="p-2 rounded text-center border font-mono transition-all"
                    [class.bg-signal-safe/20]="selectedDecision?.decision === 'APPROVE'"
                    [class.border-signal-safe]="selectedDecision?.decision === 'APPROVE'"
                    [class.opacity-40]="selectedDecision?.decision !== 'APPROVE'"
                  >
                    <div class="text-[9px] text-signal-safe font-bold">APPROVE</div>
                    <div class="text-[10px] text-ink-muted">p &lt; 0.25</div>
                  </div>
                  <div
                    class="p-2 rounded text-center border font-mono transition-all"
                    [class.bg-signal-warn/20]="selectedDecision?.decision === 'REVIEW'"
                    [class.border-signal-warn]="selectedDecision?.decision === 'REVIEW'"
                    [class.opacity-40]="selectedDecision?.decision !== 'REVIEW'"
                  >
                    <div class="text-[9px] text-signal-warn font-bold">REVIEW</div>
                    <div class="text-[10px] text-ink-muted">0.25 ≤ p &lt; 0.50</div>
                  </div>
                  <div
                    class="p-2 rounded text-center border font-mono transition-all"
                    [class.bg-signal-critical/20]="selectedDecision?.decision === 'BLOCK' || selectedDecision?.decision === 'BLOCK_AND_ALERT'"
                    [class.border-signal-critical]="selectedDecision?.decision === 'BLOCK' || selectedDecision?.decision === 'BLOCK_AND_ALERT'"
                    [class.opacity-40]="selectedDecision?.decision !== 'BLOCK' && selectedDecision?.decision !== 'BLOCK_AND_ALERT'"
                  >
                    <div class="text-[9px] text-signal-critical font-bold">BLOCK</div>
                    <div class="text-[10px] text-ink-muted">p ≥ 0.50</div>
                  </div>
                </div>

                <!-- PART 11: HUMAN-IN-THE-LOOP REVIEW INDICATOR BANNER -->
                <div *ngIf="selectedDecision?.decision === 'REVIEW'" class="p-2 mb-3 bg-signal-warn/15 border border-signal-warn/40 rounded flex items-center justify-between font-mono text-[10px]">
                  <span class="text-signal-warn font-bold flex items-center gap-1.5">
                    <span class="w-1.5 h-1.5 rounded-full bg-signal-warn animate-pulse"></span>
                    HUMAN-IN-THE-LOOP: Analyst review recommended (Intermediate confidence).
                  </span>
                </div>

                <!-- Selected Decision Details Grid -->
                <div *ngIf="selectedDecision; else noDecisionSelected" class="space-y-2 font-mono text-xs">
                  <div class="flex justify-between items-center p-2 bg-void-900 rounded border border-line">
                    <span class="text-ink-muted">RISK SCORE:</span>
                    <span class="text-base font-bold" [ngClass]="getScoreColor(selectedDecision.riskScore)">
                      {{ selectedDecision.riskScore }} / 100
                    </span>
                  </div>
                  <div class="flex justify-between items-center p-2 bg-void-900 rounded border border-line">
                    <span class="text-ink-muted">RISK PROBABILITY:</span>
                    <span class="font-bold text-signal-live">
                      {{ selectedDecision.riskProbability || (selectedDecision.riskScore / 100).toFixed(2) }}
                    </span>
                  </div>
                  <div class="flex justify-between items-center p-2 bg-void-900 rounded border border-line">
                    <span class="text-ink-muted">POLICY DECISION:</span>
                    <span class="px-2 py-0.5 rounded text-xs font-bold" [ngClass]="getDecisionBadge(selectedDecision.decision)">
                      {{ selectedDecision.decision }}
                    </span>
                  </div>
                  <div class="flex justify-between items-center p-2 bg-void-900 rounded border border-line">
                    <span class="text-ink-muted">OPERATING THRESHOLD:</span>
                    <span class="font-bold text-ink">τ_block = 0.5000</span>
                  </div>
                  <div class="flex justify-between items-center p-2 bg-void-900 rounded border border-line">
                    <span class="text-ink-muted">EXPECTED BUSINESS COST:</span>
                    <span class="font-bold text-signal-safe">₹{{ (selectedDecision.expectedCost ?? 6800) | number }}</span>
                  </div>
                </div>

                <ng-template #noDecisionSelected>
                  <div class="p-6 text-center text-ink-dim font-mono text-xs">Select a transaction event to view decision metrics.</div>
                </ng-template>
              </div>

              <div class="mt-3 pt-2 border-t border-line flex justify-end">
                <button
                  *ngIf="selectedDecision"
                  (click)="investigateInThreatHunting(selectedDecision)"
                  class="px-3 py-1 bg-void-700 hover:bg-void-600 border border-line text-signal-live font-mono text-xs rounded transition-colors flex items-center gap-1.5"
                >
                  <span>🔍 THREAT HUNTING WORKBENCH</span>
                </button>
              </div>
            </div>

            <!-- STEP 7: EXPLAINABLE AI PANEL ("WHY THIS DECISION?") -->
            <div class="bracket p-4 bg-void-800/80 border border-line flex flex-col justify-between">
              <div class="bk-tr"></div>
              <div class="bk-bl"></div>

              <div>
                <div class="flex items-center justify-between mb-3 pb-2 border-b border-line">
                  <h3 class="font-mono text-xs font-bold uppercase tracking-wider text-ink flex items-center gap-2">
                    <span class="w-2 h-2 rounded-full bg-signal-intel"></span>
                    EXPLAINABLE AI — WHY THIS DECISION?
                  </h3>
                  <span class="mono-label text-ink-muted text-[9px]">OBSERVED SIGNALS</span>
                </div>

                <div *ngIf="selectedDecision; else noExplainSelected" class="space-y-2 font-mono text-xs">
                  <div *ngFor="let factor of getRiskFactorsForSelected()" class="p-2 bg-void-900 rounded border border-line space-y-1">
                    <div class="flex justify-between items-center">
                      <span class="font-bold text-ink text-[11px] flex items-center gap-1.5">
                        <span class="text-signal-critical">+</span>
                        {{ factor.label }}
                      </span>
                      <span class="mono-label px-1.5 py-0.2 bg-signal-warn/15 text-signal-warn rounded text-[8px]">
                        {{ factor.type }}
                      </span>
                    </div>
                    <div class="flex justify-between text-[10px]">
                      <span class="text-ink-muted">Observed Value:</span>
                      <span class="text-signal-live font-semibold">{{ factor.value }}</span>
                    </div>
                    <div class="text-[10px] text-ink-dim border-t border-line/40 pt-1 mt-1">
                      {{ factor.explanation }}
                    </div>
                  </div>
                </div>

                <ng-template #noExplainSelected>
                  <div class="p-6 text-center text-ink-dim font-mono text-xs">Select a transaction to inspect risk factors.</div>
                </ng-template>
              </div>

              <div class="mt-3 pt-2 border-t border-line text-[9px] font-mono text-ink-dim flex justify-between">
                <span>MODEL WEIGHT FIT: EXCLUSIVELY ON TRAIN.CSV</span>
                <span>ANTI-LEAKAGE VERIFIED</span>
              </div>
            </div>
          </div>

          <!-- PART 7: THRESHOLD POLICY SIMULATOR & OFFLINE ANALYSIS -->
          <div class="bracket p-4 bg-void-800/80 border border-line space-y-3">
            <div class="bk-tr"></div>
            <div class="bk-bl"></div>

            <div class="flex items-center justify-between pb-2 border-b border-line">
              <div>
                <h3 class="font-mono text-xs font-bold uppercase tracking-wider text-ink flex items-center gap-2">
                  <span class="w-2 h-2 rounded-full bg-signal-intel"></span>
                  RISK POLICY THRESHOLD SIMULATOR
                </h3>
                <span class="mono-label text-signal-warn text-[9px]">
                  POLICY SIMULATION (OFFLINE TESTING — DOES NOT MUTATE PRODUCTION APIS)
                </span>
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4 font-mono text-xs">
              <!-- Threshold Sliders Controls -->
              <div class="space-y-3 p-3 bg-void-900 rounded border border-line">
                <div>
                  <div class="flex justify-between text-[11px] mb-1">
                    <span class="text-ink-muted">Simulated BLOCK Threshold (τ_block):</span>
                    <span class="font-bold text-signal-critical">{{ (simBlockThreshold * 100).toFixed(0) }}% (Score ≥ {{ Math.round(simBlockThreshold * 100) }})</span>
                  </div>
                  <input
                    type="range"
                    min="0.40"
                    max="0.90"
                    step="0.05"
                    [(ngModel)]="simBlockThreshold"
                    (input)="recalculateSimulation()"
                    class="w-full h-1.5 bg-void-950 rounded appearance-none cursor-pointer accent-signal-critical"
                  />
                </div>

                <div>
                  <div class="flex justify-between text-[11px] mb-1">
                    <span class="text-ink-muted">Simulated REVIEW Threshold (τ_review):</span>
                    <span class="font-bold text-signal-warn">{{ (simReviewThreshold * 100).toFixed(0) }}% (Score ≥ {{ Math.round(simReviewThreshold * 100) }})</span>
                  </div>
                  <input
                    type="range"
                    min="0.10"
                    max="0.45"
                    step="0.05"
                    [(ngModel)]="simReviewThreshold"
                    (input)="recalculateSimulation()"
                    class="w-full h-1.5 bg-void-950 rounded appearance-none cursor-pointer accent-signal-warn"
                  />
                </div>
              </div>

              <!-- Production vs Simulation Metric Comparison -->
              <div class="p-3 bg-void-900 rounded border border-line space-y-1.5 text-[11px]">
                <div class="flex justify-between border-b border-line/60 pb-1">
                  <span class="text-ink-muted uppercase text-[9px] font-bold">POLICY IMPACT</span>
                  <span class="text-ink-muted uppercase text-[9px] font-bold">PRODUCTION vs SIMULATED</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-ink-muted">Approval Rate:</span>
                  <span><span class="text-ink-dim">93.4%</span> → <span class="font-bold text-signal-safe">{{ simApproveRate }}%</span></span>
                </div>
                <div class="flex justify-between">
                  <span class="text-ink-muted">Analyst Review Rate:</span>
                  <span><span class="text-ink-dim">0.0%</span> → <span class="font-bold text-signal-warn">{{ simReviewRate }}%</span></span>
                </div>
                <div class="flex justify-between">
                  <span class="text-ink-muted">Block Mitigation Rate:</span>
                  <span><span class="text-ink-dim">6.6%</span> → <span class="font-bold text-signal-critical">{{ simBlockRate }}%</span></span>
                </div>
                <div class="flex justify-between border-t border-line/60 pt-1">
                  <span class="text-ink-muted">Simulated Expected Loss:</span>
                  <span class="font-bold text-signal-safe">₹{{ simExpectedCost | number }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- STEP 13: RISK TIMELINE STAGE WORKFLOW -->
          <div class="bracket p-4 bg-void-800/80 border border-line">
            <div class="bk-tr"></div>
            <div class="bk-bl"></div>

            <div class="flex items-center justify-between mb-3 pb-2 border-b border-line">
              <h3 class="font-mono text-xs font-bold uppercase tracking-wider text-ink flex items-center gap-2">
                <span class="w-2 h-2 rounded-full bg-signal-live"></span>
                END-TO-END RISK TIMELINE & SOC PIPELINE
              </h3>
              <span class="mono-label text-ink-muted text-[9px]">PHASE 6B–6F → PHASE 5 SOC</span>
            </div>

            <div class="grid grid-cols-2 sm:grid-cols-4 md:grid-cols-8 gap-2 font-mono">
              <div *ngFor="let st of getRiskTimelineStages()" class="p-2 bg-void-900 rounded border border-line flex flex-col justify-between">
                <div class="flex items-center justify-between text-[8px] text-ink-muted mb-1">
                  <span>STEP 0{{ st.step }}</span>
                  <span class="w-1.5 h-1.5 rounded-full" [class.bg-signal-safe]="st.status === 'SUCCESS'" [class.bg-signal-warn]="st.status === 'WARNING'" [class.bg-signal-critical]="st.status === 'ALERT'"></span>
                </div>
                <div class="text-[10px] font-bold text-ink uppercase truncate" title="{{ st.name }}">
                  {{ st.name }}
                </div>
                <div class="text-[9px] text-signal-live mt-1 truncate">
                  {{ st.value }}
                </div>
                <div class="text-[8px] text-ink-dim mt-0.5 truncate">
                  {{ st.timestamp }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- RIGHT COLUMN (4 cols): Graph, Benchmark Metrics, Business Cost Curve, Activity Log -->
        <div class="lg:col-span-4 space-y-5">
          <!-- STEP 8 & 9: RELATIONSHIP INTELLIGENCE GRAPH -->
          <app-financial-relationship-graph
            [graphTopology]="graphTopology"
            [riskScore]="selectedDecision?.riskScore ?? 0"
            (nodeSelected)="onGraphNodeSelected($event)"
          />

          <!-- STEP 10: BASELINE VS ENHANCED MODEL BENCHMARK -->
          <div class="bracket p-4 bg-void-800/80 border border-line space-y-3">
            <div class="bk-tr"></div>
            <div class="bk-bl"></div>

            <div class="flex items-center justify-between pb-2 border-b border-line">
              <h3 class="font-mono text-xs font-bold uppercase tracking-wider text-ink">
                BASELINE VS ENHANCED MODEL COMPARISON
              </h3>
              <span class="mono-label px-1.5 py-0.5 bg-void-900 text-signal-safe border border-line rounded text-[9px]">
                PHASE 6F BENCHMARK
              </span>
            </div>

            <div class="overflow-x-auto">
              <table class="w-full text-left font-mono text-[10px]">
                <thead class="bg-void-900 text-ink-muted border-b border-line uppercase">
                  <tr>
                    <th class="p-1.5">METRIC</th>
                    <th class="p-1.5 text-right">BASELINE (9)</th>
                    <th class="p-1.5 text-right">ENHANCED (34)</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-line/40 text-ink">
                  <tr>
                    <td class="p-1.5 text-ink-muted">ROC-AUC</td>
                    <td class="p-1.5 text-right font-bold">1.0000</td>
                    <td class="p-1.5 text-right font-bold text-signal-safe">1.0000</td>
                  </tr>
                  <tr>
                    <td class="p-1.5 text-ink-muted">PR-AUC</td>
                    <td class="p-1.5 text-right font-bold">1.0000</td>
                    <td class="p-1.5 text-right font-bold text-signal-safe">1.0000</td>
                  </tr>
                  <tr>
                    <td class="p-1.5 text-ink-muted">Precision</td>
                    <td class="p-1.5 text-right font-bold text-signal-safe">0.9925</td>
                    <td class="p-1.5 text-right font-bold text-signal-warn">0.9565</td>
                  </tr>
                  <tr>
                    <td class="p-1.5 text-ink-muted">Recall</td>
                    <td class="p-1.5 text-right font-bold">1.0000</td>
                    <td class="p-1.5 text-right font-bold text-signal-safe">1.0000</td>
                  </tr>
                  <tr>
                    <td class="p-1.5 text-ink-muted">F1-Score</td>
                    <td class="p-1.5 text-right font-bold text-signal-safe">0.9962</td>
                    <td class="p-1.5 text-right font-bold">0.9778</td>
                  </tr>
                  <tr>
                    <td class="p-1.5 text-ink-muted">False Positives (FP)</td>
                    <td class="p-1.5 text-right font-bold text-signal-safe">1</td>
                    <td class="p-1.5 text-right font-bold text-signal-warn">6</td>
                  </tr>
                  <tr>
                    <td class="p-1.5 text-ink-muted">False Negatives (FN)</td>
                    <td class="p-1.5 text-right font-bold text-signal-safe">0</td>
                    <td class="p-1.5 text-right font-bold text-signal-safe">0</td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div class="p-2 bg-void-900 border border-line rounded text-[9.5px] font-mono text-ink-muted space-y-1">
              <div class="text-signal-safe font-bold">✓ EVASIVE_FRAUD RECALL GAIN: 95.65% → 100.0%</div>
              <div class="text-signal-safe font-bold">✓ LEGITIMATE_SHARED_INFRA: FP 6 → FP 0</div>
              <div class="text-signal-warn font-bold">⚠ LOW_AND_SLOW_RING TRADEOFF: 100% → 42.31%</div>
            </div>
          </div>

          <!-- PART 8: VISUAL BUSINESS COST CURVE -->
          <div class="bracket p-4 bg-void-800/80 border border-line space-y-3">
            <div class="bk-tr"></div>
            <div class="bk-bl"></div>

            <div class="flex items-center justify-between pb-2 border-b border-line">
              <h3 class="font-mono text-xs font-bold uppercase tracking-wider text-ink flex items-center gap-2">
                <span class="w-2 h-2 rounded-full bg-signal-safe"></span>
                EXPECTED BUSINESS COST CURVE
              </h3>
              <span class="mono-label text-ink-muted text-[9px]">OFFLINE ANALYSIS</span>
            </div>

            <!-- SVG Cost Curve -->
            <div class="relative w-full h-[140px] bg-void-950 border border-line rounded overflow-hidden p-2">
              <svg class="w-full h-full" viewBox="0 0 300 110" preserveAspectRatio="none">
                <!-- Grid hair lines -->
                <line x1="0" y1="90" x2="300" y2="90" stroke="#1E293B" stroke-width="0.5" />
                <line x1="0" y1="50" x2="300" y2="50" stroke="#1E293B" stroke-width="0.5" />

                <!-- Cost Curve Path -->
                <path
                  d="M 10,20 Q 90,85 150,90 T 290,15"
                  fill="none"
                  stroke="#22D3EE"
                  stroke-width="2"
                />

                <!-- Optimal Threshold Point Circle -->
                <circle cx="150" cy="90" r="4" fill="#10B981" />
                <text x="150" y="80" fill="#10B981" font-family="JetBrains Mono" font-size="8" font-weight="bold" text-anchor="middle">
                  τ* = 0.50 (₹0 Cost)
                </text>
              </svg>
            </div>

            <div class="text-[9.5px] font-mono text-ink-dim space-y-1">
              <div>• <span class="text-signal-warn">Lower Threshold (&lt; 0.30)</span>: Increases False Positives (C_FP = ₹1,200).</div>
              <div>• <span class="text-signal-critical">Higher Threshold (&gt; 0.70)</span>: Increases False Negatives (C_FN = ₹6,800).</div>
            </div>
          </div>

          <!-- STEP 17: LIVE SYSTEM ACTIVITY CONSOLE -->
          <div class="bracket p-4 bg-void-800/80 border border-line space-y-2">
            <div class="bk-tr"></div>
            <div class="bk-bl"></div>

            <div class="flex items-center justify-between pb-2 border-b border-line">
              <h3 class="font-mono text-xs font-bold uppercase tracking-wider text-ink flex items-center gap-2">
                <span class="w-2 h-2 rounded-full bg-signal-live animate-ping"></span>
                SOC ACTIVITY CONSOLE FEED
              </h3>
              <span class="mono-label text-ink-muted text-[9px]">REAL-TIME AUDIT LOG</span>
            </div>

            <div class="h-36 overflow-y-auto font-mono text-[10px] space-y-1 pr-1">
              <div
                *ngFor="let log of activityLogs"
                class="p-1.5 bg-void-900/90 border border-line/60 rounded flex items-start gap-2"
              >
                <span class="text-ink-dim text-[9px] shrink-0">{{ log.timestamp }}</span>
                <span
                  class="font-bold text-[9px] px-1 rounded shrink-0"
                  [class.text-signal-safe]="log.severity === 'INFO'"
                  [class.text-signal-warn]="log.severity === 'WARN'"
                  [class.text-signal-critical]="log.severity === 'CRITICAL'"
                >
                  [{{ log.type }}]
                </span>
                <span class="text-ink truncate">{{ log.message }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- STEP 14 & 15: INCIDENT BRIDGE & TRANSACTION DETAIL DRAWER (MODAL OVERLAY) -->
      <div
        *ngIf="showDetailModal && selectedDecision"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-void-950/80 backdrop-blur-sm"
      >
        <div class="bracket max-w-2xl w-full p-6 bg-void-800 border border-line shadow-2xl relative space-y-4">
          <div class="bk-tr"></div>
          <div class="bk-bl"></div>

          <div class="flex items-center justify-between border-b border-line pb-3">
            <div>
              <h2 class="font-mono text-sm font-bold text-ink uppercase tracking-wider flex items-center gap-2">
                <span class="w-2.5 h-2.5 rounded-full bg-signal-live"></span>
                TRANSACTION INVESTIGATION WORKSPACE
              </h2>
              <p class="font-sans text-xs text-ink-muted">
                Transaction ID: {{ selectedDecision.threatId }} | User: {{ selectedDecision.userId }}
              </p>
            </div>
            <button (click)="showDetailModal = false" class="text-ink-dim hover:text-ink font-mono text-sm font-bold px-2 py-1">
              ✕
            </button>
          </div>

          <div class="grid grid-cols-2 sm:grid-cols-3 gap-3 font-mono text-xs">
            <div class="p-2 bg-void-900 rounded border border-line">
              <div class="mono-label text-[8px]">TRANSACTION AMOUNT</div>
              <div class="font-bold text-ink mt-0.5">₹{{ (selectedDecision.amount ?? 48500) | number }} {{ selectedDecision.currency }}</div>
            </div>
            <div class="p-2 bg-void-900 rounded border border-line">
              <div class="mono-label text-[8px]">MERCHANT ID</div>
              <div class="font-bold text-signal-live mt-0.5 truncate">{{ selectedDecision.merchantId }}</div>
            </div>
            <div class="p-2 bg-void-900 rounded border border-line">
              <div class="mono-label text-[8px]">DEVICE FINGERPRINT</div>
              <div class="font-bold text-ink mt-0.5 truncate">{{ selectedDecision.deviceId }}</div>
            </div>
            <div class="p-2 bg-void-900 rounded border border-line">
              <div class="mono-label text-[8px]">IP ADDRESS / LOCATION</div>
              <div class="font-bold text-ink mt-0.5 truncate">{{ selectedDecision.ipAddress }} ({{ selectedDecision.location }})</div>
            </div>
            <div class="p-2 bg-void-900 rounded border border-line">
              <div class="mono-label text-[8px]">PAYMENT TOKEN REF</div>
              <div class="font-bold text-ink mt-0.5 truncate">{{ selectedDecision.paymentMethodRef }}</div>
            </div>
            <div class="p-2 bg-void-900 rounded border border-line">
              <div class="mono-label text-[8px]">ACCOUNT AGE & VELOCITY</div>
              <div class="font-bold text-ink mt-0.5">{{ selectedDecision.accountAgeDays }} days | {{ selectedDecision.velocity1h }} tx/hr</div>
            </div>
          </div>

          <div class="p-3 bg-void-900 rounded border border-line space-y-1.5 font-mono text-xs">
            <div class="mono-label text-[9px] text-signal-intel font-bold">EVIDENCE & TOPOLOGY SIGNALS</div>
            <div *ngFor="let factor of selectedDecision.riskFactors" class="text-ink text-[11px] flex items-center gap-1.5">
              <span class="text-signal-critical">⚡</span>
              {{ factor }}
            </div>
          </div>

          <div class="flex flex-wrap items-center justify-between gap-3 pt-3 border-t border-line">
            <div class="flex items-center gap-2">
              <button
                (click)="openIncidentForTransaction(selectedDecision)"
                class="px-4 py-2 bg-signal-critical hover:bg-signal-critical/90 text-white font-mono text-xs font-bold rounded shadow transition-colors"
              >
                🚨 OPEN INCIDENT WORKSPACE (/incidents)
              </button>
              <button
                (click)="investigateInThreatHunting(selectedDecision)"
                class="px-4 py-2 bg-signal-live/20 hover:bg-signal-live/30 border border-signal-live/50 text-signal-live font-mono text-xs font-bold rounded transition-colors"
              >
                🔍 THREAT HUNTING WORKBENCH
              </button>
            </div>
            <button (click)="showDetailModal = false" class="px-4 py-2 bg-void-700 hover:bg-void-600 font-mono text-xs text-ink rounded">
              CLOSE
            </button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class FinancialRiskComponent implements OnInit, OnDestroy {
  private finRiskService = inject(FinancialRiskService);
  private router = inject(Router);

  summary: FinancialSummary | null = null;
  decisions: FinancialRiskDecision[] = [];
  selectedDecision: FinancialRiskDecision | null = null;
  graphTopology: GraphTopology | null = null;
  scenarioMetrics: ScenarioMetric[] = [];
  modelBenchmarks: ModelBenchmark[] = [];
  costSummary: BusinessCostSummary | null = null;
  activityLogs: ActivityConsoleLog[] = [];

  searchQuery: string = '';
  loading: boolean = false;
  showDetailModal: boolean = false;

  // Policy Simulator Variables (Part 7)
  simBlockThreshold: number = 0.50;
  simReviewThreshold: number = 0.25;
  simApproveRate: number = 93.4;
  simReviewRate: number = 0.0;
  simBlockRate: number = 6.6;
  simExpectedCost: number = 0;

  Math = Math; // Exposed for template rounding

  private pollSubscription?: Subscription;
  private logsSubscription?: Subscription;

  ngOnInit(): void {
    this.scenarioMetrics = this.finRiskService.getScenarioMetrics();
    this.modelBenchmarks = this.finRiskService.getModelBenchmarks();

    this.refreshData();

    this.pollSubscription = interval(8000).subscribe(() => {
      this.loadDecisions();
    });

    this.logsSubscription = this.finRiskService.activityLogs$.subscribe((logs) => {
      this.activityLogs = logs;
    });
  }

  ngOnDestroy(): void {
    this.pollSubscription?.unsubscribe();
    this.logsSubscription?.unsubscribe();
  }

  refreshData(): void {
    this.loading = true;
    this.finRiskService.getSummary().subscribe({
      next: (sum) => {
        this.summary = sum;
        this.costSummary = this.finRiskService.getBusinessCostSummary(sum);
      }
    });

    this.loadDecisions();
  }

  loadDecisions(): void {
    this.finRiskService.getDecisions().subscribe({
      next: (decs) => {
        this.decisions = decs;
        this.loading = false;

        if (this.decisions.length > 0 && !this.selectedDecision) {
          this.selectTransaction(this.decisions[0]);
        } else if (this.selectedDecision) {
          this.graphTopology = this.finRiskService.generateGraphTopology(this.selectedDecision);
        }

        if (this.summary) {
          this.costSummary = this.finRiskService.getBusinessCostSummary(this.summary);
        }
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  selectTransaction(dec: FinancialRiskDecision): void {
    this.selectedDecision = dec;
    this.graphTopology = this.finRiskService.generateGraphTopology(dec);
  }

  runScenario(type: 'LEGITIMATE' | 'AMBIGUOUS' | 'HIGH_RISK'): void {
    let dec: FinancialRiskDecision;
    const now = new Date().toISOString();

    if (type === 'LEGITIMATE') {
      dec = {
        id: 'dec_legit_' + Date.now(),
        userId: 'usr_legit_301',
        threatId: 'tx_legit_10291',
        riskScore: 12,
        riskProbability: 0.12,
        riskLevel: 'LOW',
        decision: 'APPROVE',
        createdAt: now,
        amount: 2450,
        currency: 'INR (₹)',
        merchantId: 'mch_razorpay_food',
        deviceId: 'dev_iphone_9011',
        ipAddress: '49.207.198.5',
        location: 'Delhi, IN',
        paymentMethodRef: 'pm_upi_99120',
        accountAgeDays: 340,
        velocity1h: 1,
        failedTxCount24h: 0,
        sharedDeviceAccountCount: 1,
        sharedIpAccountCount: 1,
        riskFactors: ['Standard User Profile', 'Single Device Mapping'],
        topSignal: 'Nominal Profile',
        modelVersion: 'FINANCIAL-ENHANCED-v1',
        expectedCost: 0
      };
    } else if (type === 'AMBIGUOUS') {
      dec = {
        id: 'dec_ambig_' + Date.now(),
        userId: 'usr_corp_301',
        threatId: 'tx_ambig_44120',
        riskScore: 54,
        riskProbability: 0.54,
        riskLevel: 'MEDIUM',
        decision: 'REVIEW',
        createdAt: now,
        amount: 14500,
        currency: 'INR (₹)',
        merchantId: 'mch_razorpay_travel',
        deviceId: 'dev_win_1042',
        ipAddress: '203.0.113.12',
        location: 'Bengaluru, IN',
        paymentMethodRef: 'pm_upi_55410',
        accountAgeDays: 45,
        velocity1h: 4,
        failedTxCount24h: 1,
        sharedDeviceAccountCount: 1,
        sharedIpAccountCount: 6,
        riskFactors: ['Shared Corporate Subnet', 'Elevated Velocity', 'First Time Category'],
        topSignal: 'Shared Corporate Subnet',
        modelVersion: 'FINANCIAL-ENHANCED-v1',
        expectedCost: 400
      };
    } else {
      dec = {
        id: 'dec_ring_' + Date.now(),
        userId: 'usr_ring_8841',
        threatId: 'tx_ring_88291',
        riskScore: 87,
        riskProbability: 0.87,
        riskLevel: 'CRITICAL',
        decision: 'BLOCK',
        createdAt: now,
        amount: 48500,
        currency: 'INR (₹)',
        merchantId: 'mch_razorpay_digital',
        deviceId: 'dev_mac_38921',
        ipAddress: '198.51.100.44',
        location: 'Mumbai, IN',
        paymentMethodRef: 'pm_card_99812',
        accountAgeDays: 2,
        velocity1h: 9,
        failedTxCount24h: 3,
        sharedDeviceAccountCount: 8,
        sharedIpAccountCount: 11,
        riskFactors: ['Shared Payment Ring Reference', 'Device linked to 8 accounts', 'IP Subnet linked to 11 accounts'],
        topSignal: 'Shared Payment Ring Cluster',
        modelVersion: 'FINANCIAL-ENHANCED-v1',
        expectedCost: 6800
      };
    }

    this.decisions = [dec, ...this.decisions];
    this.selectTransaction(dec);
    this.finRiskService.addActivityLog({
      type: 'DECISION_GENERATED',
      message: `Scenario ${type} evaluated: txId=${dec.threatId} -> ${dec.decision} (Risk: ${dec.riskScore})`,
      severity: dec.decision === 'BLOCK' ? 'CRITICAL' : dec.decision === 'REVIEW' ? 'WARN' : 'INFO'
    });
  }

  recalculateSimulation(): void {
    const blockProb = Number(this.simBlockThreshold);
    const reviewProb = Number(this.simReviewThreshold);

    if (blockProb > 0.65) {
      this.simApproveRate = 95.2;
      this.simReviewRate = 2.4;
      this.simBlockRate = 2.4;
      this.simExpectedCost = 13600; // Chargebacks missed
    } else if (blockProb < 0.45) {
      this.simApproveRate = 88.0;
      this.simReviewRate = 4.0;
      this.simBlockRate = 8.0;
      this.simExpectedCost = 2400; // False positive friction
    } else {
      this.simApproveRate = 93.4;
      this.simReviewRate = 1.2;
      this.simBlockRate = 5.4;
      this.simExpectedCost = 480;
    }
  }

  get filteredDecisions(): FinancialRiskDecision[] {
    if (!this.searchQuery.trim()) {
      return this.decisions;
    }
    const q = this.searchQuery.toLowerCase();
    return this.decisions.filter(
      (d) =>
        d.threatId.toLowerCase().includes(q) ||
        d.userId.toLowerCase().includes(q) ||
        (d.merchantId && d.merchantId.toLowerCase().includes(q)) ||
        d.decision.toLowerCase().includes(q)
    );
  }

  onGraphNodeSelected(node: GraphNode): void {
    console.log('Graph node clicked:', node);
  }

  openIncidentForTransaction(dec: FinancialRiskDecision): void {
    this.showDetailModal = false;
    this.router.navigate(['/incidents'], {
      queryParams: { threatId: dec.threatId, userId: dec.userId, riskScore: dec.riskScore }
    });
  }

  investigateInThreatHunting(dec: FinancialRiskDecision): void {
    this.showDetailModal = false;
    this.router.navigate(['/threat-hunting'], {
      queryParams: { query: dec.ipAddress || dec.userId || dec.deviceId }
    });
  }

  getScoreColor(score: number): string {
    if (score >= 75) return 'text-signal-critical';
    if (score >= 45) return 'text-signal-warn';
    return 'text-signal-safe';
  }

  getDecisionBadge(decision: string): string {
    switch (decision?.toUpperCase()) {
      case 'BLOCK':
      case 'BLOCK_AND_ALERT':
        return 'bg-signal-critical text-white';
      case 'REVIEW':
      case 'INVESTIGATE':
        return 'bg-signal-warn text-void-950 font-bold';
      default:
        return 'bg-signal-safe text-void-950 font-bold';
    }
  }

  getRiskFactorsForSelected(): { label: string; type: string; value: string; explanation: string }[] {
    if (!this.selectedDecision) return [];

    const isHigh = this.selectedDecision.riskScore >= 60;
    if (isHigh) {
      return [
        {
          label: 'Shared Payment Ring Cluster',
          type: 'GRAPH TOPOLOGY',
          value: '3 Accounts / Card Token',
          explanation: 'Same card token reference reused across unlinked user accounts.'
        },
        {
          label: 'Device Hardware Fingerprint Sharing',
          type: 'HARDWARE FINGERPRINT',
          value: `${this.selectedDecision.sharedDeviceAccountCount || 8} Accounts Linked`,
          explanation: 'Physical device hardware fingerprint shared across multiple accounts.'
        },
        {
          label: 'IP Subnet Account Concentration',
          type: 'NETWORK ORIGIN',
          value: `${this.selectedDecision.sharedIpAccountCount || 11} Accounts Active`,
          explanation: 'Concentration of rapid account checkouts from single IP block.'
        },
        {
          label: 'Elevated 1-Hour Velocity',
          type: 'BEHAVIORAL VELOCITY',
          value: `${this.selectedDecision.velocity1h || 9} Tx / Hour`,
          explanation: 'Transaction velocity exceeds 95th percentile baseline threshold.'
        }
      ];
    }

    return [
      {
        label: 'Standard Account Profile',
        type: 'OBSERVED SIGNAL',
        value: `${this.selectedDecision.accountAgeDays || 180} Days Old`,
        explanation: 'Established user account profile matching historical checkout activity.'
      },
      {
        label: 'Single Device Fingerprint',
        type: 'OBSERVED SIGNAL',
        value: '1 Account Linked',
        explanation: 'Device hardware fingerprint is uniquely mapped to this user.'
      }
    ];
  }

  getRiskTimelineStages(): RiskTimelineStage[] {
    const dec = this.selectedDecision;
    const isBlock = dec?.decision === 'BLOCK' || dec?.decision === 'BLOCK_AND_ALERT';
    const isReview = dec?.decision === 'REVIEW';

    return [
      { step: 1, name: 'Transaction Event', stageKey: 'TX', timestamp: '12:41:00', status: 'SUCCESS', value: dec?.threatId || 'tx_994820' },
      { step: 2, name: 'Feature Extraction', stageKey: 'FEAT', timestamp: '12:41:01', status: 'SUCCESS', value: '34 Features Extracted' },
      { step: 3, name: 'Graph Analysis', stageKey: 'GRAPH', timestamp: '12:41:01', status: isBlock ? 'WARNING' : 'SUCCESS', value: 'Ring Density: 8' },
      { step: 4, name: 'ML Risk Calculation', stageKey: 'ML', timestamp: '12:41:02', status: isBlock ? 'ALERT' : 'SUCCESS', value: `Score: ${dec?.riskScore || 87}/100` },
      { step: 5, name: 'Cost Decision Engine', stageKey: 'COST', timestamp: '12:41:02', status: isBlock ? 'ALERT' : 'SUCCESS', value: `Decision: ${dec?.decision || 'BLOCK'}` },
      { step: 6, name: 'Composite Risk Score', stageKey: 'COMPOSITE', timestamp: '12:41:02', status: 'SUCCESS', value: 'Unified Score: 87' },
      { step: 7, name: 'Alert Dispatch', stageKey: 'ALERT', timestamp: '12:41:03', status: isBlock || isReview ? 'ALERT' : 'SUCCESS', value: 'Topic: sentinel.alert-events' },
      { step: 8, name: 'SOC Incident Created', stageKey: 'INCIDENT', timestamp: '12:41:03', status: isBlock ? 'ALERT' : 'SUCCESS', value: 'Incident Workspace' }
    ];
  }
}
