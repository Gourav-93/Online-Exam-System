const API_BASE_URL = 'http://localhost:8080';

const api = {
    async request(endpoint, options = {}) {
        const token = localStorage.getItem('token');
        const headers = {
            'Content-Type': 'application/json',
            ...(options.headers || {})
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        try {
            const response = await fetch(`${API_BASE_URL}${endpoint}`, {
                ...options,
                headers
            });

            if (response.status === 401 || response.status === 403) {
                // Unauthorized or Forbidden, clear token and redirect to login
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                window.location.href = 'login.html';
                throw new Error('Authentication failed');
            }

            if (!response.ok) {
                let errorData;
                try {
                    errorData = await response.json();
                } catch(e) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                throw new Error(errorData.message || 'Request failed');
            }

            // Check if response is empty (e.g. 204 No Content)
            const text = await response.text();
            return text ? JSON.parse(text) : {};

        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    },

    get(endpoint) {
        return this.request(endpoint, { method: 'GET' });
    },

    post(endpoint, data) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },

    put(endpoint, data) {
        return this.request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },

    delete(endpoint) {
        return this.request(endpoint, { method: 'DELETE' });
    }
};

// Utils
function getUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
}

function requireAuth(role = null) {
    const token = localStorage.getItem('token');
    const user = getUser();

    if (!token || !user) {
        window.location.href = 'login.html';
        return;
    }

    if (role && user.role !== role) {
        if (user.role === 'ADMIN') {
            window.location.href = 'admin.html';
        } else {
            window.location.href = 'student.html';
        }
    }
}
