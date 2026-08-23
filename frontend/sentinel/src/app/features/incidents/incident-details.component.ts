import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { IncidentActionType, IncidentStatus } from '../../core/models/incident.model';
import { IncidentService } from '../../core/services/incident.service';

@Component({
  selector: 'stn-incident-details',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <div class="space-y-6">
      <!-- Top Navigation & Breadcrumb -->
      <div class="flex items-center justify-between border-b border-line pb-3">
        <div class="flex items-center gap-3">
          <a [routerLink]="['/incidents']" class="font-mono text-xs text-ink-muted hover:text-signal-live transition-colors">
            ← Incidents
          </a>
          <span class="text-ink-dim">/</span>
          <span class="font-mono text-xs font-bold text-signal-intel">{{ incident()?.incidentNumber || 'INC-XXXX-XXXX' }}</span>
        </div>

        <div class="flex items-center gap-2">
          <span class="mono-label text-xs">STATUS:</span>
          <span
            class="font-mono text-xs font-bold px-3 py-1 rounded border uppercase"
            [ngClass]="{
              'border-signal-critical/40 text-signal-critical bg-signal-critical/15': incident()?.status === 'OPEN',
              'border-signal-warning/40 text-signal-warning bg-signal-warning/15': incident()?.status === 'ACKNOWLEDGED',
              'border-signal-live/40 text-signal-live bg-signal-live/15': incident()?.status === 'INVESTIGATING',
              'border-signal-intel/40 text-signal-intel bg-signal-intel/15': incident()?.status === 'MITIGATED',
              'border-signal-safe/40 text-signal-safe bg-signal-safe/15': incident()?.status === 'RESOLVED' || incident()?.status === 'CLOSED'
            }"
          >
            {{ incident()?.status }}
          </span>
        </div>
      </div>

      @if (incident(); as inc) {
        <!-- Header Banner -->
        <div class="bracket p-6 bg-void-800/80 border-l-4 border-l-signal-critical flex flex-col lg:flex-row lg:items-center justify-between gap-6">
          <div class="space-y-2 flex-1">
            <div class="flex flex-wrap items-center gap-3">
              <span class="font-mono text-sm font-bold text-signal-intel bg-signal-intel/15 px-2.5 py-1 rounded border border-signal-intel/40">
                {{ inc.incidentNumber }}
              </span>
              <span
                class="font-mono text-xs font-bold px-2.5 py-1 rounded uppercase"
                [ngClass]="{
                  'bg-signal-critical/20 text-signal-critical border border-signal-critical/40': inc.severity === 'CRITICAL',
                  'bg-signal-warning/20 text-signal-warning border border-signal-warning/40': inc.severity === 'HIGH',
                  'bg-signal-intel/20 text-signal-intel border border-signal-intel/40': inc.severity === 'MEDIUM'
                }"
              >
                {{ inc.severity }} SEVERITY
              </span>
            </div>

            <h1 class="text-2xl font-bold text-ink tracking-tight">{{ inc.title }}</h1>
            <p class="text-sm text-ink-muted">{{ inc.description }}</p>
          </div>

          <div class="flex items-center gap-6 shrink-0 border-t lg:border-t-0 lg:border-l border-line pt-4 lg:pt-0 lg:pl-6">
            <div class="text-center">
              <div class="mono-label text-[10px] text-ink-muted">RISK SCORE</div>
              <div class="font-mono text-3xl font-bold text-signal-critical mt-0.5">{{ inc.riskScore }}<span class="text-xs text-ink-dim">/100</span></div>
            </div>

            <div class="text-center border-l border-line pl-6">
              <div class="mono-label text-[10px] text-ink-muted">AI CONFIDENCE</div>
              <div class="font-mono text-3xl font-bold text-signal-live mt-0.5">{{ inc.aiConfidence }}<span class="text-xs text-ink-dim">%</span></div>
            </div>
          </div>
        </div>

        <!-- Grid Workspace Layout -->
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">

          <!-- Left Column (2 cols): Evidence + Attack Timeline + Analyst Action Log -->
          <div class="lg:col-span-2 space-y-6">

            <!-- Evidence Panel -->
            <div class="bracket p-5 bg-void-800/60 space-y-4">
              <div class="flex items-center justify-between border-b border-line pb-2">
                <h3 class="font-mono text-xs font-bold text-ink uppercase tracking-wider flex items-center gap-2">
                  <span class="w-2 h-2 rounded-full bg-signal-intel"></span>
                  <span>Forensic Evidence Panel</span>
                </h3>
                <span class="mono-label text-[10px]">AFFECTED TARGET CONTEXT</span>
              </div>

              <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <div class="bracket p-3 bg-void-900/60">
                  <div class="mono-label text-[9px]">AFFECTED USER</div>
                  <div class="font-mono text-xs font-bold text-ink mt-1 truncate">{{ inc.affectedUser || 'N/A' }}</div>
                </div>
                <div class="bracket p-3 bg-void-900/60">
                  <div class="mono-label text-[9px]">AFFECTED IP ADDRESS</div>
                  <div class="font-mono text-xs font-bold text-signal-warning mt-1 truncate">{{ inc.affectedIp || 'N/A' }}</div>
                </div>
                <div class="bracket p-3 bg-void-900/60">
                  <div class="mono-label text-[9px]">DEVICE / AGENT</div>
                  <div class="font-mono text-xs font-bold text-ink mt-1 truncate">{{ inc.affectedDevice || 'N/A' }}</div>
                </div>
              </div>

              @if (inc.evidenceJson) {
                <div class="bracket p-3 bg-void-900/80 font-mono text-xs text-signal-live space-y-1">
                  <div class="mono-label text-[9px] text-ink-muted">RAW EVIDENCE PAYLOAD</div>
                  <pre class="overflow-x-auto whitespace-pre-wrap text-[11px]">{{ inc.evidenceJson }}</pre>
                </div>
              }
            </div>

            <!-- Attack Progression Timeline -->
            <div class="bracket p-5 bg-void-800/60 space-y-4">
              <div class="flex items-center justify-between border-b border-line pb-2">
                <h3 class="font-mono text-xs font-bold text-ink uppercase tracking-wider flex items-center gap-2">
                  <span class="w-2 h-2 rounded-full bg-signal-live animate-pulse"></span>
                  <span>Investigation Attack Timeline</span>
                </h3>
                <span class="mono-label text-[10px]">CHRONOLOGICAL EVOLUTION</span>
              </div>

              <div class="relative pl-6 space-y-6 before:absolute before:left-2.5 before:top-2 before:bottom-2 before:w-0.5 before:bg-line">
                @for (evt of inc.timeline; track evt.id) {
                  <div class="relative group">
                    <!-- Event Node Bullet -->
                    <div
                      class="absolute -left-6 top-1 w-3 h-3 rounded-full border-2 bg-void-900 transition-all"
                      [ngClass]="{
                        'border-signal-critical bg-signal-critical/30': evt.eventType.includes('CRITICAL') || evt.eventType.includes('IMPOSSIBLE'),
                        'border-signal-warning bg-signal-warning/30': evt.eventType.includes('DETECTED') || evt.eventType.includes('FAILED'),
                        'border-signal-live bg-signal-live/30': evt.eventType.includes('AI') || evt.eventType.includes('ACTION'),
                        'border-signal-intel bg-signal-intel/30': evt.eventType.includes('ALERT') || evt.eventType.includes('ASSIGNED')
                      }"
                    ></div>

                    <div class="bracket p-3 bg-void-900/80 hover:bg-void-900 border-line transition-all space-y-1">
                      <div class="flex items-center justify-between gap-2">
                        <span class="font-mono text-xs font-bold text-signal-live">{{ evt.eventType }}</span>
                        <span class="font-mono text-[10px] text-ink-dim">{{ evt.timestamp | date:'mediumTime' }}</span>
                      </div>
                      <p class="text-xs text-ink">{{ evt.summary }}</p>
                      <div class="flex items-center justify-between text-[10px] font-mono text-ink-muted pt-1">
                        <span>Source: <strong class="text-ink-dim">{{ evt.source }}</strong></span>
                        @if (evt.detailsJson) {
                          <span class="truncate max-w-[250px] text-ink-dim">{{ evt.detailsJson }}</span>
                        }
                      </div>
                    </div>
                  </div>
                }

                @if (!inc.timeline || inc.timeline.length === 0) {
                  <div class="text-xs text-ink-muted italic font-mono">No timeline events recorded yet.</div>
                }
              </div>
            </div>

            <!-- Analyst Audit Log Stream -->
            @if (inc.actions && inc.actions.length > 0) {
              <div class="bracket p-5 bg-void-800/60 space-y-3">
                <h3 class="font-mono text-xs font-bold text-ink uppercase tracking-wider flex items-center gap-2 border-b border-line pb-2">
                  <span class="w-2 h-2 rounded-full bg-signal-safe"></span>
                  <span>SOC Analyst Action Audit Log</span>
                </h3>

                <div class="space-y-2">
                  @for (act of inc.actions; track act.id) {
                    <div class="p-2.5 rounded bg-void-900/60 border border-line/60 flex items-center justify-between gap-3 font-mono text-xs">
                      <div class="space-y-0.5">
                        <div class="flex items-center gap-2">
                          <span class="font-bold text-signal-safe uppercase">[{{ act.actionType }}]</span>
                          <span class="text-ink font-semibold">{{ act.target }}</span>
                        </div>
                        <div class="text-[11px] text-ink-muted">{{ act.details }}</div>
                      </div>
                      <div class="text-right text-[10px] text-ink-dim shrink-0">
                        <div>By: {{ act.performedBy }}</div>
                        <div>{{ act.timestamp | date:'shortTime' }}</div>
                      </div>
                    </div>
                  }
                </div>
              </div>
            }

          </div>

          <!-- Right Column (1 col): Analyst Response Controls + Status Workflow + Notes -->
          <div class="space-y-6">

            <!-- Analyst Response Action Control Box -->
            <div class="bracket p-5 bg-void-800/80 border-t-2 border-t-signal-live space-y-4">
              <h3 class="font-mono text-xs font-bold text-ink uppercase tracking-wider border-b border-line pb-2">
                SOC Analyst Mitigation Actions
              </h3>

              <div class="grid grid-cols-2 gap-2">
                <button
                  (click)="executeAction('ACKNOWLEDGE')"
                  class="p-2.5 rounded-sm font-mono text-xs font-bold bg-signal-warning/15 text-signal-warning border border-signal-warning/40 hover:bg-signal-warning/25 transition-all text-center"
                >
                  ACKNOWLEDGE
                </button>

                <button
                  (click)="showAssignModal.set(true)"
                  class="p-2.5 rounded-sm font-mono text-xs font-bold bg-signal-intel/15 text-signal-intel border border-signal-intel/40 hover:bg-signal-intel/25 transition-all text-center"
                >
                  ASSIGN ANALYST
                </button>

                <button
                  (click)="executeAction('BLOCK_IP')"
                  class="p-2.5 rounded-sm font-mono text-xs font-bold bg-signal-critical/15 text-signal-critical border border-signal-critical/40 hover:bg-signal-critical/25 transition-all text-center"
                >
                  🚫 BLOCK IP
                </button>

                <button
                  (click)="executeAction('DISABLE_USER')"
                  class="p-2.5 rounded-sm font-mono text-xs font-bold bg-signal-critical/15 text-signal-critical border border-signal-critical/40 hover:bg-signal-critical/25 transition-all text-center"
                >
                  🔒 LOCK USER
                </button>

                <button
                  (click)="executeAction('RESET_PASSWORD')"
                  class="p-2.5 rounded-sm font-mono text-xs font-bold bg-signal-warning/15 text-signal-warning border border-signal-warning/40 hover:bg-signal-warning/25 transition-all text-center"
                >
                  🔑 RESET PASS
                </button>

                <button
                  (click)="executeAction('RESOLVE')"
                  class="p-2.5 rounded-sm font-mono text-xs font-bold bg-signal-safe/15 text-signal-safe border border-signal-safe/40 hover:bg-signal-safe/25 transition-all text-center"
                >
                  ✓ RESOLVE
                </button>
              </div>
            </div>

            <!-- Workflow Status Transition Box -->
            <div class="bracket p-5 bg-void-800/80 space-y-3">
              <h3 class="font-mono text-xs font-bold text-ink uppercase tracking-wider border-b border-line pb-2">
                Workflow Lifecycle Status
              </h3>

              <div class="space-y-1.5">
                @for (st of statusOptions; track st) {
                  <button
                    (click)="changeStatus(st)"
                    [disabled]="inc.status === st"
                    class="w-full px-3 py-2 rounded-sm font-mono text-xs font-bold border text-left transition-all flex items-center justify-between disabled:opacity-40"
                    [class]="inc.status === st
                      ? 'border-signal-live bg-signal-live/20 text-signal-live'
                      : 'border-line text-ink-muted hover:text-ink hover:border-ink-dim bg-void-900/40'"
                  >
                    <span>{{ st }}</span>
                    @if (inc.status === st) {
                      <span class="text-signal-live font-bold">CURRENT</span>
                    }
                  </button>
                }
              </div>
            </div>

            <!-- Investigation Notes Box -->
            <div class="bracket p-5 bg-void-800/80 space-y-3">
              <h3 class="font-mono text-xs font-bold text-ink uppercase tracking-wider border-b border-line pb-2 flex items-center justify-between">
                <span>Investigation Notes</span>
                <span class="text-ink-dim font-normal">({{ inc.notes?.length || 0 }})</span>
              </h3>

              <!-- Note Input Form -->
              <div class="space-y-2">
                <textarea
                  [(ngModel)]="noteContent"
                  placeholder="Type investigation findings or notes..."
                  rows="3"
                  class="w-full p-2.5 rounded-sm bg-void-900 border border-line text-xs font-mono text-ink placeholder:text-ink-dim focus:outline-none focus:border-signal-live transition-colors"
                ></textarea>
                <button
                  (click)="submitNote()"
                  [disabled]="!noteContent.trim()"
                  class="w-full py-2 rounded-sm font-mono text-xs font-bold bg-signal-intel/20 text-signal-intel border border-signal-intel/40 hover:bg-signal-intel/30 transition-all disabled:opacity-40"
                >
                  Post Note
                </button>
              </div>

              <!-- Notes Stream -->
              <div class="space-y-2 pt-2 max-h-72 overflow-y-auto pr-1">
                @for (note of inc.notes; track note.id) {
                  <div class="p-3 rounded bg-void-900/80 border border-line space-y-1">
                    <div class="flex items-center justify-between font-mono text-[10px] text-ink-dim border-b border-line/40 pb-1">
                      <span class="font-bold text-signal-intel">{{ note.author }}</span>
                      <span>{{ note.createdAt | date:'short' }}</span>
                    </div>
                    <p class="text-xs text-ink whitespace-pre-wrap pt-1 font-sans">{{ note.content }}</p>
                  </div>
                }

                @if (!inc.notes || inc.notes.length === 0) {
                  <div class="text-xs text-ink-muted italic font-mono text-center py-2">No notes added yet.</div>
                }
              </div>
            </div>

          </div>

        </div>

        <!-- Assign Analyst Modal Dialog -->
        @if (showAssignModal()) {
          <div class="fixed inset-0 bg-void-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
            <div class="bracket p-6 bg-void-800 border-line w-full max-w-md space-y-4">
              <h3 class="font-mono text-sm font-bold text-ink border-b border-line pb-2">Assign Analyst to {{ inc.incidentNumber }}</h3>
              <div class="space-y-2 font-mono text-xs">
                <label class="mono-label">ANALYST USERNAME</label>
                <input
                  type="text"
                  [(ngModel)]="assignAnalystName"
                  placeholder="e.g. sarah_connor"
                  class="w-full p-2.5 rounded bg-void-900 border border-line text-ink text-xs focus:outline-none focus:border-signal-live"
                />
              </div>
              <div class="flex items-center justify-end gap-2 pt-2">
                <button
                  (click)="showAssignModal.set(false)"
                  class="px-4 py-2 rounded font-mono text-xs text-ink-muted border border-line hover:text-ink"
                >
                  Cancel
                </button>
                <button
                  (click)="confirmAssign()"
                  [disabled]="!assignAnalystName.trim()"
                  class="px-4 py-2 rounded font-mono text-xs font-bold bg-signal-live/20 text-signal-live border border-signal-live/40 hover:bg-signal-live/30 disabled:opacity-40"
                >
                  Confirm Assign
                </button>
              </div>
            </div>
          </div>
        }
      }
    </div>
  `,
})
export class IncidentDetailsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private incidentService = inject(IncidentService);

  incident = this.incidentService.selectedIncident;
  statusOptions: IncidentStatus[] = ['OPEN', 'ACKNOWLEDGED', 'INVESTIGATING', 'MITIGATED', 'RESOLVED', 'CLOSED'];

  noteContent = '';
  assignAnalystName = '';
  showAssignModal = signal<boolean>(false);

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.incidentService.fetchIncidentById(id).subscribe();
    }
  }

  executeAction(actionType: IncidentActionType): void {
    const inc = this.incident();
    if (!inc) return;
    this.incidentService.performAction(inc.id, actionType).subscribe();
  }

  changeStatus(status: IncidentStatus): void {
    const inc = this.incident();
    if (!inc) return;
    this.incidentService.updateStatus(inc.id, status).subscribe();
  }

  submitNote(): void {
    const inc = this.incident();
    if (!inc || !this.noteContent.trim()) return;
    this.incidentService.addNote(inc.id, this.noteContent.trim()).subscribe(() => {
      this.noteContent = '';
    });
  }

  confirmAssign(): void {
    const inc = this.incident();
    if (!inc || !this.assignAnalystName.trim()) return;
    this.incidentService.assignAnalyst(inc.id, this.assignAnalystName.trim()).subscribe(() => {
      this.showAssignModal.set(false);
      this.assignAnalystName = '';
    });
  }
}
