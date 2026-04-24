import api from './api';

const basePath = '/bookings';

// Crear nueva reserva
export const createBooking = async (bookingData) => {
    const response = await api.post(basePath, bookingData);
    return response.data;
};

// Historial del cliente
export const getMyBookings = async (userId) => {
    const response = await api.get(`${basePath}/cliente/${userId}`);
    return response.data;
};

// Modificar fechas
export const updateBooking = async (id, bookingData) => {
    const response = await api.put(`${basePath}/${id}`, bookingData);
    return response.data;
};

// Cancelar reserva
export const cancelBooking = async (id) => {
    const response = await api.delete(`${basePath}/${id}`);
    return response.data;
};

// Obtener link de redirección de PayPal
export const createPaypalLink = async (id) => {
    const response = await api.post(`${basePath}/${id}/payment/create`);
    return response.data;
};

// Generar o Capturar el pago de la reserva (server-side capture via PayPal API).
export const captureBookingPayment = async (id, paypalOrderId) => {
    const response = await api.post(`${basePath}/${id}/payment/capture?paypalOrderId=${paypalOrderId}`);
    return response.data;
};

// Confirmar pago (client-side capture ya realizado por JS SDK, solo actualiza estado en BD)
export const confirmBookingPayment = async (id, paypalOrderId) => {
    const response = await api.post(`${basePath}/${id}/payment/confirm?paypalOrderId=${paypalOrderId}`);
    return response.data;
};
