import { useState, useEffect } from 'react';
import { Edit, Trash2, Plus, X, Building, MapPin, DollarSign, Image as ImageIcon, Loader2 } from 'lucide-react';
import { getProperties, createProperty, updateProperty, deleteProperty } from '../../services/property.services';

export default function PropertyCrud() {
    const [properties, setProperties] = useState([]);
    const [loadingData, setLoadingData] = useState(true);
    const [errorMsg, setErrorMsg] = useState('');
    const [isSaving, setIsSaving] = useState(false);

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [propertyToDelete, setPropertyToDelete] = useState(null);
    const [modalMode, setModalMode] = useState('create');

    const [formData, setFormData] = useState({ id: null, name: '', type: 'Hotel', roomType: 'Suite', location: '', price: '', description: '', image: '', status: 'Disponible' });

    useEffect(() => {
        fetchProperties();
    }, []);

    const fetchProperties = async () => {
        setLoadingData(true);
        setErrorMsg('');
        try {
            const data = await getProperties();
            setProperties(data);
        } catch (error) {
            console.error("Error al cargar propiedades", error);
            setErrorMsg('No se pudieron cargar las propiedades.');
        } finally {
            setLoadingData(false);
        }
    };

    const handleOpenCreate = () => {
        setFormData({ id: null, name: '', type: 'Hotel', roomType: 'Suite', location: '', price: '', description: '', image: '', status: 'Disponible' });
        setModalMode('create');
        setErrorMsg('');
        setIsModalOpen(true);
    };

    const handleOpenEdit = (property) => {
        setFormData(property);
        setModalMode('edit');
        setErrorMsg('');
        setIsModalOpen(true);
    };

    const handleDeleteClick = (id) => {
        setPropertyToDelete(id);
        setIsDeleteModalOpen(true);
    };

    const confirmDelete = async () => {
        setIsSaving(true);
        try {
            await deleteProperty(propertyToDelete);
            setProperties(properties.filter(p => p.id !== propertyToDelete));
            setIsDeleteModalOpen(false);
            setPropertyToDelete(null);
        } catch (error) {
            console.error("Error al eliminar", error);
            alert("No se pudo eliminar la propiedad.");
        } finally {
            setIsSaving(false);
        }
    };

    const handleSave = async (e) => {
        e.preventDefault();
        setIsSaving(true);
        setErrorMsg('');

        try {
            if (modalMode === 'create') {
                const newProp = await createProperty(formData);
                setProperties([...properties, newProp]);
            } else {
                const updated = await updateProperty(formData.id, formData);
                setProperties(properties.map(p => (p.id === formData.id ? updated : p)));
            }
            setIsModalOpen(false);
        } catch (error) {
            console.error("Error al guardar", error);
            setErrorMsg('Ocurrió un error al guardar. Verifica los datos.');
        } finally {
            setIsSaving(false);
        }
    };

    return (
        <div className="max-w-7xl mx-auto space-y-6">

            {/* Encabezado */}
            <div className="flex justify-between items-center bg-white p-6 rounded-lg shadow-sm border border-gray-100">
                <div>
                    <h2 className="text-2xl font-bold text-gray-800 flex items-center gap-2">
                        <Building className="text-secondary" /> Inventario de Propiedades
                    </h2>
                    <p className="text-gray-500 text-sm mt-1">Gestiona los hoteles, apartamentos y cabañas.</p>
                </div>
                <button onClick={handleOpenCreate} className="bg-secondary hover:opacity-90 text-white font-semibold py-2 px-4 rounded-lg flex items-center gap-2 transition-all">
                    <Plus size={20} /> Nueva Propiedad
                </button>
            </div>

            {/* Tabla */}
            <div className="bg-white rounded-lg shadow-md overflow-hidden border border-gray-100">
                <table className="w-full text-left border-collapse">
                    <thead>
                        <tr className="bg-gray-50 text-gray-600 border-b border-gray-200 text-sm uppercase tracking-wider">
                            <th className="p-4 font-semibold">Imagen</th>
                            <th className="p-4 font-semibold">Propiedad</th>
                            <th className="p-4 font-semibold">Habitación</th>
                            <th className="p-4 font-semibold">Ubicación</th>
                            <th className="p-4 font-semibold">Precio / Noche</th>
                            <th className="p-4 font-semibold">Estado</th>
                            <th className="p-4 font-semibold">Acciones</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-200">
                        {loadingData ? (
                            <tr>
                                <td colSpan="6" className="p-8 text-center text-gray-500">
                                    <div className="flex justify-center items-center gap-2">
                                        <Loader2 className="animate-spin text-secondary" /> Cargando propiedades...
                                    </div>
                                </td>
                            </tr>
                        ) : properties.length === 0 ? (
                            <tr>
                                <td colSpan="6" className="p-8 text-center text-gray-500">No hay propiedades registradas.</td>
                            </tr>
                        ) : properties.map((prop) => (
                            <tr key={prop.id} className="hover:bg-gray-50 transition-colors">
                                <td className="p-4">
                                    {prop.image ? (
                                        <img src={prop.image} alt={prop.name} className="w-16 h-12 object-cover rounded-md shadow-sm" />
                                    ) : (
                                        <div className="w-16 h-12 bg-gray-100 rounded-md flex items-center justify-center text-gray-400">
                                            <ImageIcon size={20} />
                                        </div>
                                    )}
                                </td>
                                <td className="p-4">
                                    <div className="font-bold text-gray-800">{prop.name}</div>
                                    <div className="text-xs text-gray-500 bg-gray-200 inline-block px-2 py-1 rounded mt-1">{prop.type}</div>
                                </td>
                                <td className="p-4 text-gray-600 font-medium">
                                    {prop.roomType}
                                </td>
                                <td className="p-4 text-gray-600">
                                    <div className="flex items-center gap-1">
                                        <MapPin size={16} /> {prop.location}
                                    </div>
                                </td>
                                <td className="p-4 font-bold text-secondary">
                                    <div className="flex items-center">
                                        <DollarSign size={16} /> {prop.price}
                                    </div>
                                </td>
                                <td className="p-4">
                                    <span className={`px-3 py-1 rounded-full text-xs font-bold ${prop.status === 'Disponible' ? 'bg-green-100 text-green-700' : 'bg-yellow-100 text-yellow-700'}`}>
                                        {prop.status}
                                    </span>
                                </td>
                                <td className="p-4">
                                    <div className="flex justify-start gap-3">
                                        <button onClick={() => handleOpenEdit(prop)} className="text-gray-400 hover:text-blue-600 transition-colors" title="Editar"><Edit size={20} /></button>
                                        <button onClick={() => handleDeleteClick(prop.id)} className="text-gray-400 hover:text-red-600 transition-colors" title="Eliminar"><Trash2 size={20} /></button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {/* MODAL PARA CREAR / EDITAR */}
            {isModalOpen && (
                <div className="fixed inset-0 bg-black/40 backdrop-blur-md flex items-center justify-center z-50 p-4 transition-all duration-300 overflow-y-auto">
                    <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl overflow-hidden animate-in fade-in zoom-in duration-300">
                        <div className="flex justify-between items-center p-5 border-b bg-gray-50">
                            <h3 className="text-xl font-bold text-gray-800">
                                {modalMode === 'create' ? 'Agregar Nueva Propiedad' : 'Editar Propiedad'}
                            </h3>
                            <button onClick={() => setIsModalOpen(false)} className="text-gray-400 hover:text-gray-700">
                                <X size={24} />
                            </button>
                        </div>

                        <form onSubmit={handleSave} className="p-5 space-y-4">
                            {errorMsg && (
                                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded text-sm mb-4">
                                    {errorMsg}
                                </div>
                            )}
                            <div className="grid grid-cols-2 gap-4">
                                <div className="col-span-2">
                                    <label className="block text-sm font-semibold text-gray-700 mb-1">Nombre</label>
                                    <input type="text" required value={formData.name} onChange={(e) => setFormData({ ...formData, name: e.target.value })} className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-primary outline-none" />
                                </div>
                                <div>
                                    <label className="block text-sm font-semibold text-gray-700 mb-1">Tipo de Propiedad</label>
                                    <select value={formData.type} onChange={(e) => setFormData({ ...formData, type: e.target.value })} className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-primary outline-none bg-white">
                                        <option value="Hotel">Hotel</option>
                                        <option value="Resort">Resort</option>
                                        <option value="Apartamento">Apartamento</option>
                                        <option value="Cabaña">Cabaña</option>
                                    </select>
                                </div>
                                <div>
                                    <label className="block text-sm font-semibold text-gray-700 mb-1">Tipo de Habitación</label>
                                    <select value={formData.roomType} onChange={(e) => setFormData({ ...formData, roomType: e.target.value })} className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-primary outline-none bg-white">
                                        <option value="Sencilla">Sencilla</option>
                                        <option value="Doble">Doble</option>
                                        <option value="Suite">Suite</option>
                                        <option value="Presidencial">Presidencial</option>
                                        <option value="Familiar">Familiar</option>
                                    </select>
                                </div>
                                <div className="col-span-2">
                                    <label className="block text-sm font-semibold text-gray-700 mb-1">Precio por Noche (USD)</label>
                                    <input type="number" required value={formData.price} onChange={(e) => setFormData({ ...formData, price: e.target.value })} className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-primary outline-none" />
                                </div>
                                <div className="col-span-2">
                                    <label className="block text-sm font-semibold text-gray-700 mb-1">Ubicación</label>
                                    <input type="text" required value={formData.location} onChange={(e) => setFormData({ ...formData, location: e.target.value })} className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-primary outline-none" />
                                </div>
                                <div className="col-span-2">
                                    <label className="block text-sm font-semibold text-gray-700 mb-1">URL de la Imagen</label>
                                    <input type="text" value={formData.image} onChange={(e) => setFormData({ ...formData, image: e.target.value })} className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-primary outline-none" placeholder="https://..." />
                                </div>
                            </div>

                            <div className="flex justify-end gap-3 pt-4 border-t mt-6">
                                <button type="button" onClick={() => setIsModalOpen(false)} className="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg font-semibold transition-colors">
                                    Cancelar
                                </button>
                                <button type="submit" disabled={isSaving} className="px-4 py-2 bg-secondary hover:opacity-90 text-white rounded-lg font-semibold transition-colors disabled:bg-gray-400">
                                    {isSaving ? 'Guardando...' : (modalMode === 'create' ? 'Crear Propiedad' : 'Guardar Cambios')}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* MODAL DE ELIMINACIÓN */}
            {isDeleteModalOpen && (
                <div className="fixed inset-0 bg-black/40 backdrop-blur-md flex items-center justify-center z-[60] p-4 transition-all duration-300 text-center">
                    <div className="bg-white rounded-2xl shadow-2xl w-full max-w-sm overflow-hidden animate-in fade-in zoom-in duration-300 p-6">
                        <div className="flex justify-center mb-4">
                            <div className="bg-red-100 p-4 rounded-full">
                                <Trash2 size={40} className="text-red-600" />
                            </div>
                        </div>
                        <h3 className="text-xl font-bold text-gray-800 mb-2">¿Estás seguro?</h3>
                        <p className="text-gray-500 mb-6 text-sm">
                            Esta acción no se puede deshacer. La propiedad será eliminada permanentemente del catálogo y se cancelarán reservas pendientes.
                        </p>
                        <div className="flex justify-center gap-4">
                            <button
                                onClick={() => setIsDeleteModalOpen(false)}
                                className="px-6 py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg font-semibold transition-colors"
                            >
                                Cancelar
                            </button>
                            <button
                                onClick={confirmDelete} disabled={isSaving}
                                className="px-6 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg font-semibold transition-colors shadow-lg shadow-red-200 disabled:bg-gray-400"
                            >
                                {isSaving ? 'Eliminando...' : 'Eliminar'}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}