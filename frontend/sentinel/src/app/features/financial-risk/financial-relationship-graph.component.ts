import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GraphTopology, GraphNode } from '../../core/models/financial-risk.model';

@Component({
  selector: 'app-financial-relationship-graph',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="bracket p-4 relative overflow-hidden bg-void-800/80 border border-line">
      <!-- HUD Corner Brackets -->
      <div class="bk-tr"></div>
      <div class="bk-bl"></div>

      <!-- Header & Toolbar -->
      <div class="flex flex-wrap items-center justify-between gap-2 mb-3 pb-2 border-b border-line">
        <div class="flex items-center gap-2">
          <span class="w-2.5 h-2.5 rounded-full bg-signal-intel animate-pulse"></span>
          <h3 class="font-mono text-xs font-bold uppercase tracking-wider text-ink">
            RELATIONSHIP INTELLIGENCE GRAPH
          </h3>
          <span class="mono-label px-1.5 py-0.5 bg-signal-intel/15 text-signal-intel border border-signal-intel/30 rounded text-[9px]">
            MULTI-HOP TOPOLOGY
          </span>
        </div>

        <div class="flex items-center gap-2">
          <!-- Zoom Controls -->
          <div class="flex items-center bg-void-900 border border-line rounded px-1 py-0.5">
            <button
              (click)="zoomIn()"
              class="px-2 py-0.5 font-mono text-xs text-ink-muted hover:text-signal-live transition-colors"
              title="Zoom In"
            >
              +
            </button>
            <span class="font-mono text-[10px] text-ink-dim px-1">{{ (zoomLevel * 100).toFixed(0) }}%</span>
            <button
              (click)="zoomOut()"
              class="px-2 py-0.5 font-mono text-xs text-ink-muted hover:text-signal-live transition-colors"
              title="Zoom Out"
            >
              -
            </button>
            <button
              (click)="resetZoom()"
              class="px-1.5 py-0.5 font-mono text-[9px] text-ink-dim hover:text-ink transition-colors border-l border-line ml-1"
              title="Reset View"
            >
              RST
            </button>
          </div>
        </div>
      </div>

      <!-- Graph Intelligence Indicator Badges (Step 9) -->
      <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-6 gap-2 mb-3">
        <div class="p-1.5 bg-void-900/80 border border-line rounded flex flex-col items-center">
          <span class="mono-label text-[8px]">DEVICE SHARING</span>
          <span
            class="font-mono text-[10px] font-bold mt-0.5 px-1.5 py-0.2 rounded"
            [ngClass]="getBadgeClass(getIndicatorLevel('device'))"
          >
            {{ getIndicatorLevel('device') }}
          </span>
        </div>
        <div class="p-1.5 bg-void-900/80 border border-line rounded flex flex-col items-center">
          <span class="mono-label text-[8px]">IP SHARING</span>
          <span
            class="font-mono text-[10px] font-bold mt-0.5 px-1.5 py-0.2 rounded"
            [ngClass]="getBadgeClass(getIndicatorLevel('ip'))"
          >
            {{ getIndicatorLevel('ip') }}
          </span>
        </div>
        <div class="p-1.5 bg-void-900/80 border border-line rounded flex flex-col items-center">
          <span class="mono-label text-[8px]">PAYMENT REUSE</span>
          <span
            class="font-mono text-[10px] font-bold mt-0.5 px-1.5 py-0.2 rounded"
            [ngClass]="getBadgeClass(getIndicatorLevel('payment'))"
          >
            {{ getIndicatorLevel('payment') }}
          </span>
        </div>
        <div class="p-1.5 bg-void-900/80 border border-line rounded flex flex-col items-center">
          <span class="mono-label text-[8px]">DENSITY</span>
          <span
            class="font-mono text-[10px] font-bold mt-0.5 px-1.5 py-0.2 rounded"
            [ngClass]="getBadgeClass(getIndicatorLevel('density'))"
          >
            {{ getIndicatorLevel('density') }}
          </span>
        </div>
        <div class="p-1.5 bg-void-900/80 border border-line rounded flex flex-col items-center">
          <span class="mono-label text-[8px]">RING CONNECTIVITY</span>
          <span
            class="font-mono text-[10px] font-bold mt-0.5 px-1.5 py-0.2 rounded"
            [ngClass]="getBadgeClass(getIndicatorLevel('ring'))"
          >
            {{ getIndicatorLevel('ring') }}
          </span>
        </div>
        <div class="p-1.5 bg-void-900/80 border border-line rounded flex flex-col items-center">
          <span class="mono-label text-[8px]">ENTITY NOVELTY</span>
          <span
            class="font-mono text-[10px] font-bold mt-0.5 px-1.5 py-0.2 rounded"
            [ngClass]="getBadgeClass(getIndicatorLevel('novelty'))"
          >
            {{ getIndicatorLevel('novelty') }}
          </span>
        </div>
      </div>

      <!-- Main Interactive Graph Area (SVG) -->
      <div class="relative w-full h-[320px] bg-void-950 border border-line/80 rounded overflow-hidden cursor-crosshair">
        <!-- Background Grid Pattern -->
        <svg class="absolute inset-0 w-full h-full pointer-events-none opacity-20" width="100%" height="100%">
          <defs>
            <pattern id="gridPattern" width="30" height="30" patternUnits="userSpaceOnUse">
              <path d="M 30 0 L 0 0 0 30" fill="none" stroke="#1E293B" stroke-width="0.5" />
            </pattern>
          </defs>
          <rect width="100%" height="100%" fill="url(#gridPattern)" />
        </svg>

        <!-- Dynamic SVG Topology -->
        <svg
          class="w-full h-full"
          viewBox="0 0 700 360"
          preserveAspectRatio="xMidYMid meet"
          [style.transform]="'scale(' + zoomLevel + ')'"
          style="transform-origin: center center; transition: transform 0.2s ease-out;"
        >
          <defs>
            <!-- Glow Filters for Suspicious Edges/Nodes -->
            <filter id="glow-red" x="-20%" y="-20%" width="140%" height="140%">
              <feGaussianBlur stdDeviation="3" result="blur" />
              <feComposite in="SourceGraphic" in2="blur" operator="over" />
            </filter>
            <filter id="glow-cyan" x="-20%" y="-20%" width="140%" height="140%">
              <feGaussianBlur stdDeviation="2" result="blur" />
              <feComposite in="SourceGraphic" in2="blur" operator="over" />
            </filter>
          </defs>

          <!-- Render Edges -->
          <g class="edges">
            <g *ngFor="let edge of graphTopology?.edges">
              <line
                [attr.x1]="getNodeCoords(edge.source).x"
                [attr.y1]="getNodeCoords(edge.source).y"
                [attr.x2]="getNodeCoords(edge.target).x"
                [attr.y2]="getNodeCoords(edge.target).y"
                [attr.stroke]="edge.suspicious ? '#EF4444' : '#1E293B'"
                [attr.stroke-width]="edge.suspicious ? '2' : '1'"
                [attr.stroke-dasharray]="edge.suspicious ? '4 2' : 'none'"
                [attr.filter]="edge.suspicious ? 'url(#glow-red)' : 'none'"
                class="transition-all duration-300"
              />

              <!-- Edge Midpoint Label -->
              <text
                [attr.x]="(getNodeCoords(edge.source).x + getNodeCoords(edge.target).x) / 2"
                [attr.y]="(getNodeCoords(edge.source).y + getNodeCoords(edge.target).y) / 2 - 4"
                fill="#4B5468"
                font-family="JetBrains Mono, monospace"
                font-size="8"
                text-anchor="middle"
              >
                {{ edge.label }}
              </text>
            </g>
          </g>

          <!-- Render Nodes -->
          <g class="nodes">
            <g
              *ngFor="let node of graphTopology?.nodes"
              (click)="selectNode(node)"
              class="cursor-pointer group"
            >
              <!-- Outer Pulse Ring for Suspicious Nodes -->
              <circle
                *ngIf="node.suspicious"
                [attr.cx]="node.x ?? 350"
                [attr.cy]="node.y ?? 180"
                r="24"
                fill="none"
                stroke="#EF4444"
                stroke-width="1"
                opacity="0.4"
                class="animate-ping"
              />

              <!-- Node Base Circle -->
              <circle
                [attr.cx]="node.x ?? 350"
                [attr.cy]="node.y ?? 180"
                [attr.r]="selectedNode?.id === node.id ? 20 : 16"
                [attr.fill]="getNodeFill(node)"
                [attr.stroke]="getNodeStroke(node)"
                [attr.stroke-width]="selectedNode?.id === node.id ? '3' : '1.5'"
                [attr.filter]="node.suspicious ? 'url(#glow-red)' : 'url(#glow-cyan)'"
                class="transition-all duration-200"
              />

              <!-- Node Icon Glyph -->
              <text
                [attr.x]="node.x ?? 350"
                [attr.y]="(node.y ?? 180) + 4"
                fill="#E5E9F0"
                font-family="JetBrains Mono, monospace"
                font-size="10"
                font-weight="bold"
                text-anchor="middle"
                class="pointer-events-none"
              >
                {{ getNodeGlyph(node.type) }}
              </text>

              <!-- Node Label Below -->
              <text
                [attr.x]="node.x ?? 350"
                [attr.y]="(node.y ?? 180) + 30"
                [attr.fill]="selectedNode?.id === node.id ? '#22D3EE' : '#7C8598'"
                font-family="JetBrains Mono, monospace"
                font-size="9"
                font-weight="bold"
                text-anchor="middle"
                class="pointer-events-none"
              >
                {{ node.type }}: {{ truncateId(node.id) }}
              </text>
            </g>
          </g>
        </svg>

        <!-- Selected Node Inspector Modal/Overlay (Step 8) -->
        <div
          *ngIf="selectedNode"
          class="absolute bottom-2 right-2 max-w-[260px] p-2.5 bg-void-900/95 backdrop-blur-md border border-line rounded shadow-lg text-xs space-y-1.5"
        >
          <div class="flex items-center justify-between border-b border-line pb-1">
            <span class="mono-label text-[9px] text-signal-intel font-bold">ENTITY INSPECTOR</span>
            <button (click)="selectedNode = null" class="text-ink-dim hover:text-ink font-mono text-[10px]">✕</button>
          </div>
          <div class="flex justify-between">
            <span class="text-ink-muted">ENTITY TYPE:</span>
            <span class="font-mono font-bold text-ink">{{ selectedNode.type }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-ink-muted">ENTITY ID:</span>
            <span class="font-mono text-signal-live font-semibold truncate max-w-[140px]">{{ selectedNode.id }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-ink-muted">RELATIONSHIPS:</span>
            <span class="font-mono font-bold" [class.text-signal-critical]="selectedNode.degree > 5" [class.text-signal-safe]="selectedNode.degree <= 2">
              {{ selectedNode.degree }} Connected
            </span>
          </div>
          <div class="flex justify-between">
            <span class="text-ink-muted">RECENT ACTIVITY:</span>
            <span class="font-mono text-ink text-[10px]">{{ selectedNode.activity }}</span>
          </div>
          <div *ngIf="selectedNode.suspicious" class="pt-1 border-t border-line text-[9px] text-signal-critical font-mono font-semibold flex items-center gap-1">
            <span class="w-1.5 h-1.5 bg-signal-critical rounded-full animate-ping"></span>
            HIGH RING DENSITY / SUSPICIOUS CLUSTER
          </div>
        </div>
      </div>
    </div>
  `
})
export class FinancialRelationshipGraphComponent implements OnChanges {
  @Input() graphTopology: GraphTopology | null = null;
  @Input() riskScore: number = 0;
  @Output() nodeSelected = new EventEmitter<GraphNode>();

  selectedNode: GraphNode | null = null;
  zoomLevel: number = 1.0;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['graphTopology'] && this.graphTopology?.centerEntity) {
      this.selectedNode = this.graphTopology.centerEntity;
    }
  }

  zoomIn(): void {
    if (this.zoomLevel < 1.8) {
      this.zoomLevel = Number((this.zoomLevel + 0.15).toFixed(2));
    }
  }

  zoomOut(): void {
    if (this.zoomLevel > 0.6) {
      this.zoomLevel = Number((this.zoomLevel - 0.15).toFixed(2));
    }
  }

  resetZoom(): void {
    this.zoomLevel = 1.0;
  }

  selectNode(node: GraphNode): void {
    this.selectedNode = node;
    this.nodeSelected.emit(node);
  }

  getNodeCoords(id: string): { x: number; y: number } {
    if (!this.graphTopology) return { x: 350, y: 180 };
    const found = this.graphTopology.nodes.find((n) => n.id === id);
    return found ? { x: found.x ?? 350, y: found.y ?? 180 } : { x: 350, y: 180 };
  }

  getNodeGlyph(type: string): string {
    switch (type) {
      case 'USER': return 'U';
      case 'DEVICE': return 'D';
      case 'IP': return 'IP';
      case 'PAYMENT': return 'P';
      case 'MERCHANT': return 'M';
      default: return 'E';
    }
  }

  getNodeFill(node: GraphNode): string {
    if (node.suspicious) return '#451A03'; // Warm dark amber/red
    if (node.type === 'USER') return '#0F172A';
    return '#0F1521';
  }

  getNodeStroke(node: GraphNode): string {
    if (node.suspicious) return '#EF4444';
    if (node.type === 'USER') return '#22D3EE';
    if (node.type === 'DEVICE') return '#8B5CF6';
    if (node.type === 'IP') return '#3B82F6';
    if (node.type === 'PAYMENT') return '#F59E0B';
    return '#10B981';
  }

  truncateId(id: string): string {
    if (!id) return '';
    return id.length > 12 ? id.substring(0, 10) + '..' : id;
  }

  getIndicatorLevel(type: 'device' | 'ip' | 'payment' | 'density' | 'ring' | 'novelty'): 'LOW' | 'MEDIUM' | 'HIGH' {
    if (!this.graphTopology || this.riskScore < 45) {
      return 'LOW';
    }
    if (this.riskScore >= 75) {
      return 'HIGH';
    }
    if (type === 'device' || type === 'ip' || type === 'ring') {
      return 'HIGH';
    }
    return 'MEDIUM';
  }

  getBadgeClass(level: 'LOW' | 'MEDIUM' | 'HIGH'): string {
    switch (level) {
      case 'HIGH': return 'bg-signal-critical/20 text-signal-critical border border-signal-critical/40';
      case 'MEDIUM': return 'bg-signal-warn/20 text-signal-warn border border-signal-warn/40';
      case 'LOW': return 'bg-signal-safe/20 text-signal-safe border border-signal-safe/40';
    }
  }
}
