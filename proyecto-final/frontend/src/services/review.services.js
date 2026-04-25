import api from './api';

const basePath = '/reviews';

// Crear nueva reseña
export const createReview = async (reviewData) => {
    const response = await api.post(basePath, reviewData);
    return response.data;
};

// Obtener reseñas de una propiedad
export const getReviewsByProperty = async (propiedadId) => {
    const response = await api.get(`${basePath}/propiedad/${propiedadId}`);
    return response.data;
};

// Obtener promedio y total de calificaciones
export const getPropertyRating = async (propiedadId) => {
    const response = await api.get(`${basePath}/propiedad/${propiedadId}/rating`);
    return response.data;
};

// Eliminar reseña
export const deleteReview = async (id) => {
    const response = await api.delete(`${basePath}/${id}`);
    return response.data;
};
