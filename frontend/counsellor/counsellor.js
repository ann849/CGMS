document.addEventListener('DOMContentLoaded', async () => {
    if (!Auth.isAuthenticated()) {
        window.location.href = '../index.html';
        return;
    }

    const user = Auth.getUser();
    if (user.role !== 'COUNSELLOR' && user.role !== 'ADMIN') {
        alert('Access denied. Counsellor authorization required.');
        Auth.redirectToDashboard(user.role);
        return;
    }

    document.getElementById('counsellorName').textContent = user.fullName || user.email;

    try {
        const res = await apiCall('/counsellor/profile');
        if (res.success && res.data) {
            console.log('Counsellor Profile Data:', res.data);
            const details = res.data.details;
            if (details) {
                document.getElementById('qualification').textContent = details.qualification || 'Not set';
                document.getElementById('specialization').textContent = details.specialization || 'Not set';
                document.getElementById('experienceYears').textContent = (details.experienceYears || 0) + ' Years';
            }
        }
    } catch (err) {
        console.error('Error fetching counsellor profile:', err);
    }

    document.getElementById('logoutBtn').addEventListener('click', (e) => {
        e.preventDefault();
        Auth.logout();
    });
});
