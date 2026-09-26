// Main Index Page JavaScript

document.addEventListener('DOMContentLoaded', () => {
    // Check if user is already logged in
    if (Auth.isAuthenticated()) {
        const user = Auth.getUser();
        if (user && user.role) {
            // Uncomment to auto-redirect logged in users:
            // Auth.redirectToDashboard(user.role);
        }
    }

    // Role selector handling for registration
    let selectedRole = 'STUDENT';
    const roleOptions = document.querySelectorAll('.role-badge-option');
    const roleInput = document.getElementById('registerRole');

    roleOptions.forEach(option => {
        option.addEventListener('click', () => {
            roleOptions.forEach(opt => opt.classList.remove('active'));
            option.classList.add('active');
            selectedRole = option.getAttribute('data-role');
            if (roleInput) roleInput.value = selectedRole;

            // Toggle role-specific fields
            const studentFields = document.getElementById('studentFields');
            const counsellorFields = document.getElementById('counsellorFields');
            const adminFields = document.getElementById('adminFields');

            if (studentFields) studentFields.classList.add('d-none');
            if (counsellorFields) counsellorFields.classList.add('d-none');
            if (adminFields) adminFields.classList.add('d-none');

            if (selectedRole === 'STUDENT' && studentFields) studentFields.classList.remove('d-none');
            if (selectedRole === 'COUNSELLOR' && counsellorFields) counsellorFields.classList.remove('d-none');
            if (selectedRole === 'ADMIN' && adminFields) adminFields.classList.remove('d-none');
        });
    });

    // Login Form Handler
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const alertBox = document.getElementById('authAlert');
            const email = document.getElementById('loginEmail').value.trim();
            const password = document.getElementById('loginPassword').value.trim();

            if (!email || !password) {
                showAlert('Please fill in all fields.', 'danger');
                return;
            }

            showAlert('Logging in...', 'info');
            const res = await Auth.login(email, password);

            if (res.success) {
                showAlert('Login successful! Redirecting...', 'success');
                setTimeout(() => Auth.redirectToDashboard(res.role), 1000);
            } else {
                showAlert(res.message, 'danger');
            }
        });
    }

    // Register Form Handler
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const fullName = document.getElementById('registerName').value.trim();
            const email = document.getElementById('registerEmail').value.trim();
            const password = document.getElementById('registerPassword').value.trim();
            const phone = document.getElementById('registerPhone').value.trim();
            const role = selectedRole;

            const userData = { fullName, email, password, phone, role };

            if (role === 'STUDENT') {
                userData.educationLevel = document.getElementById('registerEducationLevel')?.value || '';
                userData.preferredField = document.getElementById('registerPreferredField')?.value || '';
            } else if (role === 'COUNSELLOR') {
                userData.qualification = document.getElementById('registerQualification')?.value || '';
                userData.specialization = document.getElementById('registerSpecialization')?.value || '';
            } else if (role === 'ADMIN') {
                userData.department = document.getElementById('registerDepartment')?.value || '';
            }

            showAlert('Creating account...', 'info');
            const res = await Auth.register(userData);

            if (res.success) {
                showAlert('Registration successful! Redirecting...', 'success');
                setTimeout(() => Auth.redirectToDashboard(res.role), 1000);
            } else {
                showAlert(res.message, 'danger');
            }
        });
    }

    function showAlert(msg, type) {
        const alertBox = document.getElementById('authAlert');
        if (alertBox) {
            alertBox.className = `alert alert-${type} mt-3 mb-0`;
            alertBox.textContent = msg;
            alertBox.classList.remove('d-none');
        }
    }
});
