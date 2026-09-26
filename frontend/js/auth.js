// Authentication Logic for CGMS

const Auth = {
    getToken() {
        return localStorage.getItem(CONFIG.STORAGE_KEY_TOKEN);
    },

    getUser() {
        const userStr = localStorage.getItem(CONFIG.STORAGE_KEY_USER);
        return userStr ? JSON.parse(userStr) : null;
    },

    isAuthenticated() {
        return !!this.getToken();
    },

    setSession(token, user) {
        localStorage.setItem(CONFIG.STORAGE_KEY_TOKEN, token);
        localStorage.setItem(CONFIG.STORAGE_KEY_USER, JSON.stringify(user));
    },

    logout() {
        localStorage.removeItem(CONFIG.STORAGE_KEY_TOKEN);
        localStorage.removeItem(CONFIG.STORAGE_KEY_USER);
        window.location.href = '/frontend/index.html';
    },

    redirectToDashboard(role) {
        switch (role) {
            case 'STUDENT':
                window.location.href = '/frontend/student/student-dashboard.html';
                break;
            case 'COUNSELLOR':
                window.location.href = '/frontend/counsellor/dashboard.html';
                break;
            case 'ADMIN':
                window.location.href = '/frontend/admin/dashboard.html';
                break;
            default:
                window.location.href = '/frontend/index.html';
        }
    },

    async login(email, password) {
        try {
            const res = await apiCall('/auth/login', 'POST', { email, password });
            if (res.success && res.data) {
                const { accessToken, id, email: userEmail, fullName, role } = res.data;
                this.setSession(accessToken, { id, email: userEmail, fullName, role });
                return { success: true, role };
            }
            return { success: false, message: res.message || 'Login failed' };
        } catch (err) {
            return { success: false, message: err.message || 'Server connection error' };
        }
    },

    async register(userData) {
        try {
            const res = await apiCall('/auth/register', 'POST', userData);
            if (res.success && res.data) {
                const { accessToken, id, email, fullName, role } = res.data;
                this.setSession(accessToken, { id, email, fullName, role });
                return { success: true, role };
            }
            return { success: false, message: res.message || 'Registration failed' };
        } catch (err) {
            return { success: false, message: err.message || 'Server connection error' };
        }
    }
};
