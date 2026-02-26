const API_BASE = '/api';

function getOptions(method, body, useCredentials = true) {
  const opts = {
    method,
    headers: { 'Content-Type': 'application/json' },
    credentials: useCredentials ? 'include' : 'omit',
  };
  if (body) opts.body = typeof body === 'string' ? body : JSON.stringify(body);
  return opts;
}

export async function login(username, password) {
  const res = await fetch(`${API_BASE}/auth/login`, getOptions('POST', { username, password }));
  const data = await res.json();
  if (!res.ok) throw new Error(data.message || 'Login failed');
  return data;
}

export async function logout() {
  await fetch(`${API_BASE}/auth/logout`, getOptions('POST'));
}

export async function me() {
  const res = await fetch(`${API_BASE}/auth/me`, getOptions('GET'));
  const data = await res.json();
  if (!res.ok) throw new Error(data.message || 'Not authenticated');
  return data;
}

export async function register(payload) {
  const res = await fetch(`${API_BASE}/auth/register`, getOptions('POST', payload));
  const contentType = res.headers.get('content-type') || '';
  if (!contentType.includes('application/json')) {
    throw new Error(
      'Server returned HTML instead of JSON. Is the backend running? Check that Tomcat is running and the app is deployed (e.g. at http://localhost:8080/oceanview).'
    );
  }
  const data = await res.json();
  if (!data.success) throw new Error(data.message || 'Registration failed');
  return data;
}

async function parseJsonResponse(res) {
  const contentType = res.headers.get('content-type') || '';
  if (!contentType.includes('application/json')) {
    const text = await res.text();
    throw new Error(text?.slice(0, 100) || 'Server did not return JSON. Is the backend running at the correct URL?');
  }
  return res.json();
}

// ==================== ROOM CATEGORIES ====================
export async function getRoomCategories() {
  const res = await fetch(`${API_BASE}/room-categories`, getOptions('GET'));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message || 'Failed to load categories');
  return data.data;
}

// ==================== ROOMS ====================
export async function getRooms(branchId = 1, available = false, checkIn, checkOut) {
  let url = `${API_BASE}/rooms?branchId=${branchId}`;
  if (available) {
    url += `&available=true&checkIn=${checkIn || ''}&checkOut=${checkOut || ''}`;
  }
  const res = await fetch(url, getOptions('GET'));
  const data = await res.json();
  if (!data.success) throw new Error(data.message);
  return data.data;
}

export async function createRoom(room) {
  const res = await fetch(`${API_BASE}/rooms`, getOptions('POST', room));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message || 'Create failed');
  return data.data;
}

export async function updateRoom(id, room) {
  const res = await fetch(`${API_BASE}/rooms?id=${id}`, getOptions('PUT', room));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message || 'Update failed');
  return data;
}

export async function deleteRoom(id) {
  const res = await fetch(`${API_BASE}/rooms?id=${id}`, getOptions('DELETE'));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message || 'Delete failed');
  return data;
}

// ==================== GUESTS ====================
export async function getGuestMe() {
  const res = await fetch(`${API_BASE}/guests/me`, getOptions('GET'));
  const data = await res.json();
  if (!data.success) throw new Error(data.message);
  return data.data;
}

// ==================== RESERVATIONS ====================
export async function getReservations(opts = {}) {
  const params = new URLSearchParams(opts);
  const res = await fetch(`${API_BASE}/reservations?${params}`, getOptions('GET'));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message || 'Failed to load reservations');
  return data.data;
}

export async function createReservation(body) {
  const res = await fetch(`${API_BASE}/reservations`, getOptions('POST', body));
  const data = await res.json();
  if (!data.success) throw new Error(data.message || 'Failed to create reservation');
  return data.data;
}

export async function approveReservation(id) {
  const res = await fetch(`${API_BASE}/reservations?id=${id}&action=approve`, getOptions('PUT'));
  const data = await res.json();
  if (!data.success) throw new Error(data.message);
  return data;
}

export async function cancelReservation(id, reason) {
  const res = await fetch(`${API_BASE}/reservations?id=${id}&action=cancel&reason=${encodeURIComponent(reason || '')}`, getOptions('PUT'));
  const data = await res.json();
  if (!data.success) throw new Error(data.message);
  return data;
}

export async function updateReservation(id, reservation) {
  const res = await fetch(`${API_BASE}/reservations?id=${id}&action=update`, getOptions('PUT', reservation));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message || 'Update failed');
  return data;
}

export async function deleteReservation(id) {
  const res = await fetch(`${API_BASE}/reservations?id=${id}`, getOptions('DELETE'));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message || 'Delete failed');
  return data;
}

// ==================== USERS ====================
export async function getUsers(role) {
  const url = role ? `${API_BASE}/users?role=${role}` : `${API_BASE}/users`;
  const res = await fetch(url, getOptions('GET'));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message);
  return data.data;
}

export async function createUser(user, password) {
  const url = `${API_BASE}/users?password=${encodeURIComponent(password)}`;
  const res = await fetch(url, getOptions('POST', user));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message || 'Create failed');
  return data.data;
}

export async function updateUser(id, user) {
  const res = await fetch(`${API_BASE}/users?id=${id}`, getOptions('PUT', user));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message || 'Update failed');
  return data.data;
}

export async function deleteUser(id) {
  const res = await fetch(`${API_BASE}/users?id=${id}`, getOptions('DELETE'));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message || 'Delete failed');
  return data;
}

// ==================== REPORTS ====================
export async function getReports(type, from, to) {
  const params = new URLSearchParams({ type });
  if (from) params.set('from', from);
  if (to) params.set('to', to);
  const res = await fetch(`${API_BASE}/reports?${params}`, getOptions('GET'));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message || 'Failed to load report');
  return data.data;
}

// ==================== MAINTENANCE REQUESTS ====================
export async function getMaintenanceRequests(opts = {}) {
  const params = new URLSearchParams(opts);
  const res = await fetch(`${API_BASE}/maintenance?${params}`, getOptions('GET'));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message || 'Failed to load maintenance requests');
  return data.data;
}

export async function createMaintenanceRequest(body) {
  const res = await fetch(`${API_BASE}/maintenance`, getOptions('POST', body));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message || 'Create failed');
  return data.data;
}

export async function updateMaintenanceStatus(id, status) {
  const res = await fetch(`${API_BASE}/maintenance?id=${id}&status=${encodeURIComponent(status)}`, getOptions('PUT'));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message || 'Update failed');
  return data;
}

// ==================== FEEDBACK ====================
export async function getFeedback(opts = {}) {
  const params = new URLSearchParams(opts);
  const res = await fetch(`${API_BASE}/feedback?${params}`, getOptions('GET'));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message || 'Failed to load feedback');
  return data.data;
}

export async function createFeedback(body) {
  const res = await fetch(`${API_BASE}/feedback`, getOptions('POST', body));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message || 'Submit failed');
  return data.data;
}

// ==================== SERVICE REQUESTS ====================
export async function getServiceRequests(opts = {}) {
  const params = new URLSearchParams(opts);
  const res = await fetch(`${API_BASE}/service-requests?${params}`, getOptions('GET'));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message || 'Failed to load service requests');
  return data.data;
}

export async function createServiceRequest(body) {
  const res = await fetch(`${API_BASE}/service-requests`, getOptions('POST', body));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message || 'Create failed');
  return data.data;
}

export async function updateServiceRequestStatus(id, status) {
  const res = await fetch(`${API_BASE}/service-requests?id=${id}&status=${encodeURIComponent(status)}`, getOptions('PUT'));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message || 'Update failed');
  return data;
}

// ==================== NOTIFICATIONS ====================
export async function getNotifications() {
  const res = await fetch(`${API_BASE}/notifications`, getOptions('GET'));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message || 'Failed to load notifications');
  return data.data;
}

export async function markNotificationRead(id) {
  const res = await fetch(`${API_BASE}/notifications?id=${id}&action=read`, getOptions('PUT'));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message || 'Failed');
  return data;
}

export async function getUnreadCount() {
  const res = await fetch(`${API_BASE}/notifications?unreadCount=true`, getOptions('GET'));
  const data = await parseJsonResponse(res);
  if (!data.success) throw new Error(data.message || 'Failed');
  return data.data.count;
}
