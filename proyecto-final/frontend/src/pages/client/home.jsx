import { useState, useEffect } from 'react';
import { MapPin, Star, Loader2, ChevronLeft, ChevronRight, Search } from 'lucide-react';
import { Link } from 'react-router-dom';
import { getProperties, searchProperties } from '../../services/property.services';
import { getPropertyRating } from '../../services/review.services';

export default function Home() {
    const [properties, setProperties] = useState([]);
    const [loadingData, setLoadingData] = useState(true);
    const [currentPage, setCurrentPage] = useState(1);
    const [searchParams, setSearchParams] = useState({ ubicacion: '', tipoHabitacion: '', precioMaximo: '', fechaInicio: '', fechaFin: '' });
    const itemsPerPage = 10;

    useEffect(() => {
        fetchProperties();
    }, []);

    const fetchProperties = async () => {
        setLoadingData(true);
        try {
            const data = await getProperties();
            const enriched = await enrichWithRatings(data);
            setProperties(enriched);
        } catch (error) {
            console.error("Error al cargar propiedades", error);
        } finally {
            setLoadingData(false);
        }
    };

    const enrichWithRatings = async (props) => {
        return Promise.all(props.map(async (p) => {
            try {
                const ratingData = await getPropertyRating(p.id);
                return { ...p, rating: ratingData.promedio, totalReviews: ratingData.total };
            } catch {
                return { ...p, rating: 0, totalReviews: 0 };
            }
        }));
    };

    const handleSearch = async () => {
        setLoadingData(true);
        setCurrentPage(1);
        try {
            if (!searchParams.ubicacion && !searchParams.tipoHabitacion && !searchParams.precioMaximo && !searchParams.fechaInicio) {
                const data = await getProperties();
                const enriched = await enrichWithRatings(data);
                setProperties(enriched);
            } else {
                const data = await searchProperties(searchParams);
                const enriched = await enrichWithRatings(data);
                setProperties(enriched);
            }
        } catch (error) {
            console.error("Error al buscar propiedades", error);
        } finally {
            setLoadingData(false);
        }
    };

    // Paginación
    const indexOfLastItem = currentPage * itemsPerPage;
    const indexOfFirstItem = indexOfLastItem - itemsPerPage;
    const currentProperties = properties.slice(indexOfFirstItem, indexOfLastItem);
    const totalPages = Math.ceil(properties.length / itemsPerPage);

    const paginate = (pageNumber) => {
        setCurrentPage(pageNumber);
        window.scrollTo({ top: 300, behavior: 'smooth' });
    };

    return (
        <div className="max-w-7xl mx-auto space-y-8">

            {/* Banner y Buscador */}
            <div className="bg-white border border-gray-100 p-10 rounded-3xl shadow-xl text-center">
                <h2 className="text-4xl font-bold mb-4 text-secondary">Encuentra tu hospedaje perfecto</h2>
                <p className="text-gray-500 mb-8 text-lg">Busca entre cientos de propiedades disponibles con confirmación inmediata.</p>
                <div className="flex flex-col md:flex-row justify-center gap-3 max-w-5xl mx-auto flex-wrap">
                    <div className="flex-grow min-w-[200px]">
                        <input type="text" placeholder="¿Ubicación? (ej. Punta Cana)" value={searchParams.ubicacion} onChange={(e) => setSearchParams({ ...searchParams, ubicacion: e.target.value })} className="px-4 py-3 rounded-xl w-full text-gray-800 bg-gray-50 border border-gray-200 outline-none focus:ring-2 focus:ring-secondary/50 transition-all font-medium" />
                    </div>
                    <div className="flex-shrink-0 w-full md:w-auto">
                        <select value={searchParams.tipoHabitacion} onChange={(e) => setSearchParams({ ...searchParams, tipoHabitacion: e.target.value })} className="px-4 py-3 rounded-xl w-full text-gray-800 bg-gray-50 border border-gray-200 outline-none focus:ring-2 focus:ring-secondary/50 transition-all font-medium">
                            <option value="">Habitación</option>
                            <option value="Sencilla">Sencilla</option>
                            <option value="Doble">Doble</option>
                            <option value="Suite">Suite</option>
                            <option value="Presidencial">Presidencial</option>
                            <option value="Familiar">Familiar</option>
                        </select>
                    </div>
                    <div className="flex-shrink-0 w-full md:w-36">
                        <input type="number" placeholder="Pr. Máx ($)" value={searchParams.precioMaximo} onChange={(e) => setSearchParams({ ...searchParams, precioMaximo: e.target.value })} className="px-4 py-3 rounded-xl w-full text-gray-800 bg-gray-50 border border-gray-200 outline-none focus:ring-2 focus:ring-secondary/50 transition-all font-medium" />
                    </div>
                    <div className="flex-shrink-0 w-full md:w-40 flex items-center bg-gray-50 border border-gray-200 rounded-xl px-2 focus-within:ring-2 focus-within:ring-secondary/50 relative text-sm">
                        <span className="text-gray-400 font-medium absolute top-[-10px] left-3 bg-white px-1 text-xs">Disponibilidad</span>
                        <input type="date" value={searchParams.fechaInicio} onChange={(e) => setSearchParams({ ...searchParams, fechaInicio: e.target.value })} className="py-3 w-full bg-transparent outline-none font-medium cursor-pointer" title="Llegada" />
                    </div>
                    <button onClick={handleSearch} className="bg-secondary hover:opacity-90 text-white font-bold px-8 py-3 rounded-xl transition-all shadow-lg shadow-secondary/20 uppercase tracking-widest text-sm flex justify-center items-center gap-2">
                        <Search size={18} /> Buscar
                    </button>
                </div>
            </div>

            {/* Título de sección */}
            <div>
                <h3 className="text-2xl font-bold text-gray-800 mb-6">Propiedades Destacadas</h3>

                {loadingData ? (
                    <div className="flex justify-center items-center py-20">
                        <Loader2 className="animate-spin text-secondary" size={40} />
                        <span className="ml-3 text-gray-500 text-lg font-medium">Buscando las mejores propiedades...</span>
                    </div>
                ) : properties.length === 0 ? (
                    <div className="text-center py-20 text-gray-500 text-lg">
                        Lo sentimos, no hay propiedades disponibles en este momento.
                    </div>
                ) : (
                    <>
                        {/* Cuadrícula de Propiedades */}
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                            {currentProperties.map((property) => (
                                <PropertyCard key={property.id} property={property} />
                            ))}
                        </div>

                        {/* Controles de paginación */}
                        {totalPages > 1 && (
                            <div className="flex justify-center items-center gap-2 mt-12 mb-8 pt-4">
                                <button
                                    onClick={() => paginate(currentPage - 1)}
                                    disabled={currentPage === 1}
                                    className="p-2 rounded-lg bg-white border-2 border-gray-100 hover:border-secondary hover:text-secondary disabled:opacity-50 disabled:cursor-not-allowed transition-all text-gray-600 focus:outline-none"
                                >
                                    <ChevronLeft size={20} />
                                </button>

                                {[...Array(totalPages)].map((_, index) => (
                                    <button
                                        key={index}
                                        onClick={() => paginate(index + 1)}
                                        className={`w-10 h-10 rounded-lg font-bold transition-all shadow-sm focus:outline-none ${currentPage === index + 1 ? 'bg-secondary text-white shadow-secondary/20' : 'bg-white border-2 border-gray-100 text-gray-600 hover:border-secondary hover:text-secondary'}`}
                                    >
                                        {index + 1}
                                    </button>
                                ))}

                                <button
                                    onClick={() => paginate(currentPage + 1)}
                                    disabled={currentPage === totalPages}
                                    className="p-2 rounded-lg bg-white border-2 border-gray-100 hover:border-secondary hover:text-secondary disabled:opacity-50 disabled:cursor-not-allowed transition-all text-gray-600 focus:outline-none"
                                >
                                    <ChevronRight size={20} />
                                </button>
                            </div>
                        )}
                    </>
                )}
            </div>

        </div>
    );
}

// Componente hijo para las Tarjetas del Catálogo
function PropertyCard({ property }) {
    return (
        <div className="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-xl transition-all duration-300 group cursor-pointer border border-gray-100">
            <div className="relative h-48 overflow-hidden">
                <img
                    src={property.image}
                    alt={property.name}
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                />
                <span className="absolute top-3 left-3 bg-secondary/90 backdrop-blur-sm px-2 py-1 rounded text-xs font-bold text-white shadow">
                    {property.roomType}
                </span>
                <span className="absolute top-3 right-3 bg-white px-2 py-1 rounded text-xs font-bold text-gray-800 shadow">
                    {property.type}
                </span>
            </div>

            <div className="p-5">
                <div className="flex justify-between items-start mb-2">
                    <h4 className="text-lg font-bold text-gray-800 line-clamp-1">{property.name}</h4>
                    <div className="flex items-center">
                        <Star size={16} className="text-amber-400 fill-amber-400" />
                        <span className="ml-1 text-sm font-bold text-gray-700">{property.rating || '0.0'}</span>
                        <span className="text-xs text-gray-400 ml-1">({property.totalReviews || 0})</span>
                    </div>
                </div>

                <div className="flex items-center text-gray-500 text-sm mb-4">
                    <MapPin size={16} className="mr-1" />
                    {property.location}
                </div>

                <div className="flex flex-col gap-4 border-t pt-4">
                    <div className="flex justify-between items-center">
                        <span className="text-2xl font-bold text-gray-800">${property.price}</span>
                        <span className="text-gray-500 text-sm"> / noche</span>
                    </div>
                    <Link to={`/property/${property.id}`} className="block text-center bg-secondary text-white px-4 py-2 rounded font-semibold hover:opacity-90 transition-all w-full shadow-lg shadow-secondary/20 uppercase text-xs tracking-widest">
                        Ver detalle
                    </Link>
                </div>
            </div>
        </div>
    );
}