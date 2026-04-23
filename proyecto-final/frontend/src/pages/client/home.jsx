import { MapPin, Star } from 'lucide-react';
import { Link } from 'react-router-dom';

// Datos falsos (Mocks) que luego vendrán de DataFaker del catalog-service
const mockProperties = [
    { id: 1, name: "Hotel Punta Cana Resort", type: "Resort", location: "Punta Cana, DR", price: 250, rating: 4.8, image: "https://images.unsplash.com/photo-1566073771259-6a8506099945?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80" },
    { id: 2, name: "City Center Loft", type: "Apartamento", location: "Santo Domingo, DR", price: 85, rating: 4.5, image: "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80" },
    { id: 3, name: "Samaná Eco Lodge", type: "Cabaña", location: "Samaná, DR", price: 120, rating: 4.9, image: "https://images.unsplash.com/photo-1587061949409-02df41d5e562?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80" },
];

export default function Home() {
    return (
        <div className="max-w-7xl mx-auto space-y-8">

            {/* Banner y Buscador */}
            <div className="bg-white border border-gray-100 p-10 rounded-3xl shadow-xl text-center">
                <h2 className="text-4xl font-bold mb-4 text-secondary">Encuentra tu hospedaje perfecto</h2>
                <p className="text-gray-500 mb-8 text-lg">Busca entre cientos de propiedades disponibles con confirmación inmediata.</p>

                <div className="flex flex-col md:flex-row justify-center gap-4 max-w-4xl mx-auto">
                    <div className="flex-grow">
                        <input type="text" placeholder="¿A dónde quieres ir?" className="px-4 py-3 rounded-xl w-full text-gray-800 bg-gray-50 border border-gray-200 outline-none focus:ring-2 focus:ring-secondary/50 transition-all font-medium" />
                    </div>
                    <div className="flex-shrink-0">
                        <input type="date" className="px-4 py-3 rounded-xl w-full md:w-48 text-gray-800 bg-gray-50 border border-gray-200 outline-none focus:ring-2 focus:ring-secondary/50 transition-all font-medium" />
                    </div>
                    <button className="bg-secondary hover:opacity-90 text-white font-bold px-10 py-3 rounded-xl transition-all shadow-lg shadow-secondary/20 uppercase tracking-widest text-sm">
                        Buscar
                    </button>
                </div>
            </div>

            {/* Título de sección */}
            <div>
                <h3 className="text-2xl font-bold text-gray-800 mb-6">Propiedades Destacadas</h3>

                {/* Cuadrícula de Propiedades */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                    {mockProperties.map((property) => (
                        <PropertyCard key={property.id} property={property} />
                    ))}
                </div>
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
                <span className="absolute top-3 right-3 bg-white px-2 py-1 rounded text-xs font-bold text-gray-800 shadow">
                    {property.type}
                </span>
            </div>

            <div className="p-5">
                <div className="flex justify-between items-start mb-2">
                    <h4 className="text-lg font-bold text-gray-800 line-clamp-1">{property.name}</h4>
                    <div className="flex items-center text-secondary">
                        <Star size={16} className="fill-current" />
                        <span className="ml-1 text-sm font-bold text-gray-700">{property.rating}</span>
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