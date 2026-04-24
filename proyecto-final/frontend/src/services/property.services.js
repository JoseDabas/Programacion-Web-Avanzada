import api from './api';

const basePath = '/catalog';

// Obtener todas las propiedades
export const getProperties = async () => {
    const response = await api.get(basePath);
    // Mapear de Propiedad (backend) a Property (frontend)
    return response.data.map(mapToFrontendProperty);
};

// Buscar propiedades con filtros
export const searchProperties = async (params) => {
    const { ubicacion, tipoHabitacion, precioMaximo, fechaInicio, fechaFin } = params;
    
    // Construir query string dinámicamente
    const query = new URLSearchParams();
    if (ubicacion) query.append('ubicacion', ubicacion);
    if (tipoHabitacion) query.append('tipoHabitacion', tipoHabitacion);
    if (precioMaximo) query.append('precioMaximo', precioMaximo);
    if (fechaInicio) query.append('fechaInicio', fechaInicio);
    if (fechaFin) query.append('fechaFin', fechaFin);

    const response = await api.get(`${basePath}/search?${query.toString()}`);
    return response.data.map(mapToFrontendProperty);
};

// Obtener una propiedad por ID
export const getPropertyById = async (id) => {
    const response = await api.get(`${basePath}/${id}`);
    return mapToFrontendProperty(response.data);
};

// Crear una nueva propiedad
export const createProperty = async (propertyData) => {
    const backendData = mapToBackendPropiedad(propertyData);
    const response = await api.post(basePath, backendData);
    return mapToFrontendProperty(response.data);
};

// Actualizar una propiedad existente
export const updateProperty = async (id, propertyData) => {
    const backendData = mapToBackendPropiedad(propertyData);
    const response = await api.put(`${basePath}/${id}`, backendData);
    return mapToFrontendProperty(response.data);
};

// Eliminar una propiedad
export const deleteProperty = async (id) => {
    const response = await api.delete(`${basePath}/${id}`);
    return response.data;
};

// Helpers para mapear los modelos
const mapToFrontendProperty = (data) => {
    return {
        id: data.id,
        name: data.nombre,
        description: data.descripcion || '',
        type: data.tipoPropiedad || 'Hotel',
        roomType: data.tipoHabitacion || 'Suite',
        location: data.ubicacion,
        price: data.precioPorNoche,
        image: data.imagenes && data.imagenes.length > 0 ? data.imagenes[0] : '',
        amenities: data.amenidades || [],
        status: 'Disponible' // El backend no maneja estado actualmente, por defecto Disponible
    };
};

const mapToBackendPropiedad = (data) => {
    return {
        id: data.id,
        nombre: data.name,
        descripcion: data.description,
        tipoPropiedad: data.type,
        tipoHabitacion: data.roomType,
        ubicacion: data.location,
        precioPorNoche: data.price,
        imagenes: data.image ? [data.image] : [],
        amenidades: data.amenities || []
    };
};
