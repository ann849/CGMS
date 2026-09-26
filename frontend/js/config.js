// Global Configuration
const CONFIG = {
    API_BASE_URL: 'http://localhost:8080/api',
    STORAGE_KEY_TOKEN: 'cgms_jwt_token',
    STORAGE_KEY_USER: 'cgms_user_info'
};

// Helper for making API calls with JWT header
async function apiCall(endpoint, method = 'GET', body = null) {
    const token = localStorage.getItem(CONFIG.STORAGE_KEY_TOKEN);
    const headers = {
        'Content-Type': 'application/json'
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const options = {
        method,
        headers
    };

    if (body) {
        options.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(`${CONFIG.API_BASE_URL}${endpoint}`, options);
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.message || `HTTP Error ${response.status}`);
        }
        
        return data;
    } catch (error) {
        console.error(`API Error (${endpoint}):`, error);
        throw error;
    }
}
