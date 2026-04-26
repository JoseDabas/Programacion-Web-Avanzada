import api from './api';

export const login = async (email, password) => {
    const response = await api.post('/auth/login', { email, password });

    if (response.data && response.data.token) {
        localStorage.setItem('token', response.data.token);
        // Guardar el rol o datos del usuario si el backend lo devuelve
        localStorage.setItem('user', JSON.stringify(response.data));
    }

    return response.data;
};

export const register = async (email, password, name, lastName) => {
    const response = await api.post('/auth/register', { email, password, name, lastName, role: 'CLIENT' });
    return response.data;
};

export const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/login';
};