import api from './api';

const basePath = '/users';

// Obtener todos los usuarios
export const getUsers = async () => {
    const response = await api.get(basePath);
    return response.data;
};

// Crear un nuevo usuario
export const createUser = async (userData) => {
    const response = await api.post(basePath, userData);
    return response.data;
};

// Actualizar un usuario existente
export const updateUser = async (id, userData) => {
    const response = await api.put(`${basePath}/${id}`, userData);
    return response.data;
};

// Eliminar un usuario
export const deleteUser = async (id) => {
    const response = await api.delete(`${basePath}/${id}`);
    return response.data;
};
