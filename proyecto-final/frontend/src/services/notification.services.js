import api from './api';

const basePath = '/notifications';

// Dispara correo de bienvenida
export const sendRegistrationEmail = async (email, nombre) => {
    const response = await api.post(`${basePath}/register`, { email, nombre });
    return response.data;
};

// Genera el reporte Jasper y envía la factura con PDF como archivo adjunto
export const sendInvoiceEmail = async (invoiceData) => {
    const response = await api.post(`${basePath}/invoice`, invoiceData);
    return response.data;
};
