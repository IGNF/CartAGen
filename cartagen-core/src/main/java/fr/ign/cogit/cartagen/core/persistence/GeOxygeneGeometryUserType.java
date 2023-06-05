/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILLC_fr.html see Licence_CeCILLC_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.persistence;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.postgis.PGgeometry;

import org.locationtech.jts.geom.Geometry;

import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.conversion.ParseException;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;

public class GeOxygeneGeometryUserType implements UserType {
	static Logger logger = LogManager.getLogger(GeOxygeneGeometryUserType.class.getName());
	private static final int[] geometryTypes = new int[] { Types.STRUCT };

	@Override
	public int[] sqlTypes() {
		return GeOxygeneGeometryUserType.geometryTypes;
	}

	/**
	 * Converts the native geometry object to a GeOxygene <code>GM_Object</code>
	 * .
	 * 
	 * @param object
	 *            native database geometry object (depends on the JDBC spatial
	 *            extension of the database)
	 * @return GeOxygene geometry corresponding to geomObj.
	 */
	@SuppressWarnings("unchecked")
	public IGeometry convert2GM_Object(Object object) {
		if (object == null) {
			return null;
		}
		PGgeometry pgGeom = null;
		if (object instanceof org.postgresql.util.PGobject) {
			try {
				pgGeom = new PGgeometry(object.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// in some cases, Postgis returns not PGgeometry objects
		// but org.postgis.Geometry instances.
		// This has been observed when retrieving GeometryCollections
		// as the result of an SQLoperation such as Union.
		if (object instanceof org.postgis.Geometry) {
			pgGeom = new PGgeometry((org.postgis.Geometry) object);
		}

		if (object instanceof PGgeometry) {
			pgGeom = (PGgeometry) object;
		}

		if (pgGeom == null) {
			return null;
		}

		try {
			/*
			 * In version 1.0.x of PostGIS, SRID is added to the beginning of
			 * the pgGeom string
			 */

			String geom = pgGeom.toString();
			String geomString = geom;
			int srid = 0;
			if (geom.indexOf("=") >= 0) {
				String subString = geom.substring(geom.indexOf("=") + 1, geom.indexOf(";")); // $NONNLS1$
																								// //$NONNLS2$
				// logger.info(subString);
				srid = Integer.parseInt(subString);
				geomString = pgGeom.toString().substring(pgGeom.toString().indexOf(";") + 1);
			}
			IGeometry geOxyGeom = WktGeOxygene.makeGeOxygene(geomString);

			if (geOxyGeom instanceof IMultiPoint) {
				IMultiPoint aggr = (IMultiPoint) geOxyGeom;
				if (aggr.size() == 1) {
					aggr.get(0);
					aggr.setCRS(srid);
					return aggr;
				}
			}

			if (geOxyGeom instanceof IMultiCurve) {
				IMultiCurve<IOrientableCurve> aggr = (IMultiCurve<IOrientableCurve>) geOxyGeom;
				if (aggr.size() == 1) {
					aggr.get(0);
					aggr.setCRS(srid);
					return aggr;
				}
			}

			if (geOxyGeom instanceof IMultiSurface) {
				IMultiSurface<IOrientableSurface> aggr = (IMultiSurface<IOrientableSurface>) geOxyGeom;
				if (aggr.size() == 1) {
					aggr.get(0);
					aggr.setCRS(srid);
					return aggr;
				}
			}
			geOxyGeom.setCRS(srid);
			return geOxyGeom;

		} catch (ParseException e) {
			GeOxygeneGeometryUserType.logger.warn("## WARNING ## Postgis to GeOxygene returns NULL ");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Converts a GeOxygene <code>GM_Object</code> to a native geometry object.
	 * 
	 * @param geom
	 *            GeOxygene GM_Object to convert
	 * @param connection
	 *            the current database connection
	 * @return native database geometry object corresponding to geom.
	 */
	public Object conv2DBGeometry(IGeometry geom, Connection connection) {
		try {
			if (geom == null) {
				return null;
			}
			String srid = "";
			if (geom.getCRS() != 1) {
				srid = "SRID=" + geom.getCRS() + ";";
			}
			// logger.info("conv2DBGeometry " + srid + geom.toString());
			PGgeometry pgGeom = new PGgeometry(srid + geom.toString());
			return pgGeom;
		} catch (SQLException e) {
			GeOxygeneGeometryUserType.logger.warn("## WARNING ## GeOxygene to Postgis returns NULL "); // $NONNLS1$
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		if (x == y) {
			return true;
		}
		if (x == null || y == null) {
			return false;
		}
		return ((IGeometry) x).equalsExact((IGeometry) y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	/*
	 * @Override public Object nullSafeGet(ResultSet rs, String[] names, Object
	 * owner) throws HibernateException, SQLException { Object geomObj =
	 * rs.getObject(names[0]); return this.convert2GM_Object(geomObj); }
	 * 
	 * @Override public void nullSafeSet(PreparedStatement st, Object value, int
	 * index) throws HibernateException, SQLException { if (value == null) {
	 * st.setNull(index, this.sqlTypes()[0]); } else { if (value instanceof
	 * IGeometry) { IGeometry geom = (IGeometry) value; Object dbGeom =
	 * this.conv2DBGeometry(geom, st.getConnection()); st.setObject(index,
	 * dbGeom); } else { try { IGeometry geom =
	 * AdapterFactory.toGM_Object((Geometry) value); Object dbGeom =
	 * this.conv2DBGeometry(geom, st.getConnection()); st.setObject(index,
	 * dbGeom); } catch (Exception e) { e.printStackTrace(); } } } }
	 */

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}

	@Override
	public Class<IGeometry> returnedClass() {
		return IGeometry.class;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor arg2, Object arg3)
			throws HibernateException, SQLException {
		Object geomObj = rs.getObject(names[0]);
		return this.convert2GM_Object(geomObj);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor arg3)
			throws HibernateException, SQLException {

		if (value == null) {
			st.setNull(index, this.sqlTypes()[0]);
		} else {
			if (value instanceof IGeometry) {
				IGeometry geom = (IGeometry) value;
				Object dbGeom = this.conv2DBGeometry(geom, st.getConnection());
				st.setObject(index, dbGeom);
			} else {
				try {
					IGeometry geom = AdapterFactory.toGM_Object((Geometry) value);
					Object dbGeom = this.conv2DBGeometry(geom, st.getConnection());
					st.setObject(index, dbGeom);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
