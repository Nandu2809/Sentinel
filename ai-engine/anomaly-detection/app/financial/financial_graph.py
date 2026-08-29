"""
Sentinel Phase 6C — In-Memory Financial Entity Relationship Graph Engine

Lightweight, temporal-aware entity relationship graph constructed from financial transaction streams.
Strictly maintains temporal ordering to prevent future-data leakage.

Entities: USER, DEVICE, IP, PAYMENT_METHOD, MERCHANT
Relationships:
- USER --USES--> DEVICE
- USER --CONNECTS_FROM--> IP
- USER --USES--> PAYMENT_METHOD
- USER --PAYS--> MERCHANT
- DEVICE --SEEN_FROM--> IP
- PAYMENT_METHOD --USED_AT--> MERCHANT
"""

import math
from collections import defaultdict
from datetime import datetime, timezone

def parse_iso_timestamp(ts_str):
    """Parse ISO-8601 timestamp string into datetime object with UTC timezone."""
    if isinstance(ts_str, datetime):
        return ts_str if ts_str.tzinfo else ts_str.replace(tzinfo=timezone.utc)
    ts_str = ts_str.replace("Z", "+00:00")
    return datetime.fromisoformat(ts_str)

class FinancialEntityGraph:
    """In-memory temporal relationship graph for financial fraud risk evaluation."""

    def __init__(self):
        # Bi-directional adjacency sets for entities
        # Entity keys formatted as "type:id", e.g. "user:usr_001", "device:dev_001"
        self.adjacency = defaultdict(set)
        
        # Timed edge records: (entity1, entity2, timestamp_dt, tx_id, user_id)
        self.edge_history = []
        
        # Entity-specific event logs with timestamps
        self.device_events = defaultdict(list)    # device_id -> [(timestamp_dt, user_id, tx_id)]
        self.ip_events = defaultdict(list)        # ip_address -> [(timestamp_dt, user_id, tx_id)]
        self.payment_events = defaultdict(list)   # payment_ref -> [(timestamp_dt, user_id, tx_id)]
        self.user_events = defaultdict(list)      # user_id -> [(timestamp_dt, device_id, ip, payment)]

        # Entity sets for quick lookup
        self.users = set()
        self.devices = set()
        self.ips = set()
        self.payment_methods = set()
        self.merchants = set()

    def add_transaction(self, event):
        """
        Incorporate transaction event into the graph.
        Must be called in chronological order.
        """
        user_id = event["userId"]
        device_id = event["deviceId"]
        ip_address = event["ipAddress"]
        payment_ref = event["paymentMethodRef"]
        merchant_id = event["merchantId"]
        tx_id = event["transactionId"]
        ts_dt = parse_iso_timestamp(event["timestamp"])

        u_node = f"user:{user_id}"
        d_node = f"device:{device_id}"
        i_node = f"ip:{ip_address}"
        p_node = f"payment:{payment_ref}"
        m_node = f"merchant:{merchant_id}"

        self.users.add(user_id)
        self.devices.add(device_id)
        self.ips.add(ip_address)
        self.payment_methods.add(payment_ref)
        self.merchants.add(merchant_id)

        # Connect bipartite edges
        edges = [
            (u_node, d_node),
            (u_node, i_node),
            (u_node, p_node),
            (u_node, m_node),
            (d_node, i_node),
            (p_node, m_node)
        ]

        for n1, n2 in edges:
            self.adjacency[n1].add(n2)
            self.adjacency[n2].add(n1)
            self.edge_history.append((n1, n2, ts_dt, tx_id, user_id))

        # Append entity timeline logs
        self.device_events[device_id].append((ts_dt, user_id, tx_id))
        self.ip_events[ip_address].append((ts_dt, user_id, tx_id))
        self.payment_events[payment_ref].append((ts_dt, user_id, tx_id))
        self.user_events[user_id].append((ts_dt, device_id, ip_address, payment_ref))

    def get_relationship_metrics(self, event):
        """
        Compute backward-looking relationship & graph features for a target event.
        Calculated using graph state up to and including the current transaction timestamp.
        """
        user_id = event["userId"]
        device_id = event["deviceId"]
        ip_address = event["ipAddress"]
        payment_ref = event["paymentMethodRef"]
        merchant_id = event["merchantId"]
        curr_ts = parse_iso_timestamp(event["timestamp"])

        u_node = f"user:{user_id}"
        d_node = f"device:{device_id}"
        i_node = f"ip:{ip_address}"
        p_node = f"payment:{payment_ref}"
        m_node = f"merchant:{merchant_id}"

        # 1. Degrees (all connected entity nodes)
        device_degree = len([n for n in self.adjacency[d_node]]) if d_node in self.adjacency else 0
        ip_degree = len([n for n in self.adjacency[i_node]]) if i_node in self.adjacency else 0
        payment_degree = len([n for n in self.adjacency[p_node]]) if p_node in self.adjacency else 0
        merchant_degree = len([n for n in self.adjacency[m_node]]) if m_node in self.adjacency else 0

        # Include current event entities if new
        if d_node not in self.adjacency:
            device_degree = max(device_degree, 2)  # Connected to user & ip in current event
        if i_node not in self.adjacency:
            ip_degree = max(ip_degree, 2)  # Connected to user & device
        if p_node not in self.adjacency:
            payment_degree = max(payment_degree, 2)  # Connected to user & merchant

        # 2. Cumulative entity counts
        # Devices linked to device_id up to current event
        dev_users = set([uid for ts, uid, tx in self.device_events[device_id] if ts <= curr_ts])
        dev_users.add(user_id)
        shared_device_account_count = len(dev_users)

        # Users linked to ip_address up to current event
        ip_users = set([uid for ts, uid, tx in self.ip_events[ip_address] if ts <= curr_ts])
        ip_users.add(user_id)
        shared_ip_account_count = len(ip_users)

        # Users linked to payment_ref up to current event
        pm_users = set([uid for ts, uid, tx in self.payment_events[payment_ref] if ts <= curr_ts])
        pm_users.add(user_id)
        shared_payment_account_count = len(pm_users)

        # Entities per User up to current event
        u_events = [ev for ev in self.user_events[user_id] if ev[0] <= curr_ts]
        user_devices = set([ev[1] for ev in u_events])
        user_devices.add(device_id)
        unique_devices_per_user = len(user_devices)

        user_ips = set([ev[2] for ev in u_events])
        user_ips.add(ip_address)
        unique_ips_per_user = len(user_ips)

        user_pms = set([ev[3] for ev in u_events])
        user_pms.add(payment_ref)
        unique_payment_methods_per_user = len(user_pms)

        # 3. Sliding Window Features (1h = 3600s, 24h = 86400s)
        window_1h = curr_ts - timedelta_seconds(3600)
        window_24h = curr_ts - timedelta_seconds(86400)

        # Recent shared counts in 1-hour window
        dev_events_1h = [ev for ev in self.device_events[device_id] if window_1h <= ev[0] <= curr_ts]
        recent_shared_device_count = len(set([ev[1] for ev in dev_events_1h] + [user_id]))
        tx_velocity_shared_device = len(dev_events_1h) + 1  # Including current tx

        ip_events_1h = [ev for ev in self.ip_events[ip_address] if window_1h <= ev[0] <= curr_ts]
        recent_shared_ip_count = len(set([ev[1] for ev in ip_events_1h] + [user_id]))
        tx_velocity_shared_ip = len(ip_events_1h) + 1

        # Recent payment reuse in 24-hour window
        pm_events_24h = [ev for ev in self.payment_events[payment_ref] if window_24h <= ev[0] <= curr_ts]
        recent_payment_reuse_count = len(set([ev[1] for ev in pm_events_24h] + [user_id]))

        # 4. Ring / Cluster Neighborhood Sizes (2-hop neighborhood size)
        device_cluster_size = self._get_2hop_cluster_size(d_node)
        ip_cluster_size = self._get_2hop_cluster_size(i_node)
        payment_cluster_size = self._get_2hop_cluster_size(p_node)

        # 5. Multi-account boolean flags
        multi_account_device_flag = 1 if shared_device_account_count > 1 else 0
        multi_account_ip_flag = 1 if shared_ip_account_count > 1 else 0
        multi_account_payment_flag = 1 if shared_payment_account_count > 1 else 0

        # 6. Combined Risk Metrics
        # Shared infrastructure score: normalized combination of shared entity counts
        shared_infra_raw = (shared_device_account_count * 0.4) + (shared_ip_account_count * 0.3) + (shared_payment_account_count * 0.5)
        shared_infrastructure_score = round(min(1.0, shared_infra_raw / 10.0), 4)

        # Relationship Density: edges / max_edges in 2-hop neighborhood
        relationship_density = self._get_neighborhood_density(u_node)

        # Ring connectivity score: log1p(dev_accounts * ip_accounts)
        ring_connectivity_score = round(math.log1p(shared_device_account_count * shared_ip_account_count), 4)

        return {
            "sharedDeviceAccountCount": shared_device_account_count,
            "sharedIpAccountCount": shared_ip_account_count,
            "sharedPaymentAccountCount": shared_payment_account_count,
            "deviceDegree": device_degree,
            "ipDegree": ip_degree,
            "paymentMethodDegree": payment_degree,
            "merchantDegree": merchant_degree,
            "uniqueDevicesPerUser": unique_devices_per_user,
            "uniqueIpsPerUser": unique_ips_per_user,
            "uniquePaymentMethodsPerUser": unique_payment_methods_per_user,
            "accountsPerDevice": shared_device_account_count,
            "accountsPerIp": shared_ip_account_count,
            "accountsPerPaymentMethod": shared_payment_account_count,
            "recentSharedDeviceCount": recent_shared_device_count,
            "recentSharedIpCount": recent_shared_ip_count,
            "recentPaymentReuseCount": recent_payment_reuse_count,
            "transactionVelocityOnSharedDevice": tx_velocity_shared_device,
            "transactionVelocityOnSharedIp": tx_velocity_shared_ip,
            "deviceClusterSize": device_cluster_size,
            "ipClusterSize": ip_cluster_size,
            "paymentClusterSize": payment_cluster_size,
            "multiAccountDeviceFlag": multi_account_device_flag,
            "multiAccountIpFlag": multi_account_ip_flag,
            "multiAccountPaymentFlag": multi_account_payment_flag,
            "sharedInfrastructureScore": shared_infrastructure_score,
            "relationshipDensity": relationship_density,
            "ringConnectivityScore": ring_connectivity_score
        }

    def _get_2hop_cluster_size(self, node_key):
        """Calculate the total number of distinct entity nodes within 2 hops of node_key."""
        if node_key not in self.adjacency:
            return 1
        hop1 = self.adjacency[node_key]
        hop2 = set(hop1)
        for n in hop1:
            hop2.update(self.adjacency[n])
        hop2.add(node_key)
        return len(hop2)

    def _get_neighborhood_density(self, node_key):
        """Calculate sub-graph edge density within 2-hop neighborhood of node_key."""
        if node_key not in self.adjacency:
            return 0.0
        hop1 = self.adjacency[node_key]
        nodes = set(hop1)
        nodes.add(node_key)
        n_count = len(nodes)
        if n_count <= 1:
            return 0.0

        actual_edges = 0
        nodes_list = list(nodes)
        for i in range(n_count):
            for j in range(i + 1, n_count):
                if nodes_list[j] in self.adjacency[nodes_list[i]]:
                    actual_edges += 1

        max_edges = (n_count * (n_count - 1)) / 2.0
        return round(actual_edges / max_edges, 4)

def timedelta_seconds(seconds):
    from datetime import timedelta
    return timedelta(seconds=seconds)
