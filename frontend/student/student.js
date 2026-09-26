document.addEventListener('DOMContentLoaded', async () => {
    if (!Auth.isAuthenticated()) {
        window.location.href = '../index.html';
        return;
    }

    const user = Auth.getUser();
    if (user.role !== 'STUDENT' && user.role !== 'ADMIN') {
        alert('Access denied. Student authorization required.');
        Auth.redirectToDashboard(user.role);
        return;
    }

    document.getElementById('userName').textContent = user.fullName || user.email;

    try {
        const res = await apiCall('/student/profile');
        if (res.success && res.data) {
            console.log('Student Profile Data:', res.data);
            const details = res.data.details;
            if (details) {
                document.getElementById('eduLevel').textContent = details.educationLevel || 'Not set';
                document.getElementById('prefField').textContent = details.preferredField || 'Not set';
                document.getElementById('careerGoal').textContent = details.careerGoal || 'Not set';
            }
        }
    } catch (err) {
        console.error('Error fetching student profile:', err);
    }

    document.getElementById('logoutBtn').addEventListener('click', (e) => {
        e.preventDefault();
        Auth.logout();
    });
});
