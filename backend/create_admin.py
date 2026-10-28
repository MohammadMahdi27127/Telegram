"""
Standalone Admin User Creation Script
This script can be run manually to create/verify admin user in PostgreSQL

Admin Credentials:
- Phone: 09052893115
- Password: 138727127
- Name: محمد مهدی
- Plan: admin

Usage:
    cd backend
    python seed_admin.py
"""

import sys
import time
import uuid
import hashlib
import secrets
import logging
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

# Setup logging
logging.basicConfig(
    level=logging.INFO,
    format='%(levelname)s: %(message)s'
)
logger = logging.getLogger(__name__)

# Import database models from main.py
try:
    from main import User, Base, get_database_url, settings
    from config import settings as cfg
except ImportError:
    logger.error("❌ Error: Could not import from main.py")
    logger.info("Make sure you run this script from the backend directory")
    sys.exit(1)

def hash_password(password: str, salt: str) -> str:
    """Hash password with salt using SHA-256."""
    return hashlib.sha256(f"{salt}{password}".encode()).hexdigest()

def create_admin_user():
    """Create or verify admin user in database."""
    
    logger.info("╔═══════════════════════════════════════════════════════╗")
    logger.info("║         Admin User Seeding Script                     ║")
    logger.info("╚═══════════════════════════════════════════════════════╝")
    logger.info("")
    
    # Admin credentials
    admin_phone = "09052893115"
    admin_password = "138727127"
    admin_name = "محمد مهدی"
    admin_username = "admin"
    
    try:
        # Verify database URL
        db_url = get_database_url(cfg.DATABASE_URL)
        is_postgresql = "postgresql" in db_url
        is_sqlite = "sqlite" in db_url
        
        logger.info(f"📊 Database Configuration:")
        logger.info(f"   Type: {'PostgreSQL' if is_postgresql else 'SQLite' if is_sqlite else 'Unknown'}")
        
        if not is_postgresql:
            logger.warning("⚠️  WARNING: Not using PostgreSQL! Using SQLite fallback.")
            logger.warning("   For production, ensure DATABASE_URL points to PostgreSQL")
        
        # Create engine and session
        engine = create_engine(db_url, echo=False, pool_pre_ping=True)
        SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
        
        # Create tables
        logger.info("")
        logger.info("🔧 Creating/Verifying database tables...")
        Base.metadata.create_all(bind=engine)
        logger.info("✅ Database tables verified")
        
        # Create session
        db = SessionLocal()
        
        # Check if admin already exists
        logger.info("")
        logger.info("🔍 Checking if admin user exists...")
        existing_admin = db.query(User).filter(User.phone == admin_phone).first()
        
        if existing_admin:
            logger.info("✅ Admin user already exists!")
            logger.info(f"   📱 Phone: {existing_admin.phone}")
            logger.info(f"   👤 Name: {existing_admin.name}")
            logger.info(f"   🆔 ID: {existing_admin.id}")
            logger.info(f"   💎 Plan: {existing_admin.plan}")
            logger.info(f"   💰 Balance: {existing_admin.balance:,} tomans")
            logger.info(f"   ⏰ Created: {existing_admin.created_at}")
            
            # Update plan if needed
            if existing_admin.plan != "admin":
                logger.warning("   ⚠️  Updating plan to 'admin'...")
                existing_admin.plan = "admin"
                db.commit()
                logger.info("   ✅ Plan updated")
            
            db.close()
            logger.info("")
            logger.info("═" * 60)
            logger.info("✅ Admin user verification complete!")
            logger.info("═" * 60)
            return True
        
        # Create new admin user
        logger.info("👨‍💼 Creating new admin user...")
        
        salt = secrets.token_hex(16)
        password_hash = hash_password(admin_password, salt)
        
        admin_user = User(
            id=str(uuid.uuid4()),
            phone=admin_phone,
            salt=salt,
            password_hash=password_hash,
            name=admin_name,
            plan="admin",
            balance=1000000,
            suspended=False,
            banner_seen=True,
            created_at=int(time.time() * 1000),
            username=admin_username
        )
        
        db.add(admin_user)
        db.commit()
        db.refresh(admin_user)
        
        logger.info("")
        logger.info("✅ Admin user created successfully!")
        logger.info(f"   📱 Phone: {admin_user.phone}")
        logger.info(f"   👤 Name: {admin_user.name}")
        logger.info(f"   🆔 ID: {admin_user.id}")
        logger.info(f"   🔐 Username: @{admin_user.username}")
        logger.info(f"   💎 Plan: {admin_user.plan}")
        logger.info(f"   💰 Balance: {admin_user.balance:,} tomans")
        logger.info(f"   ⏰ Created: {admin_user.created_at}")
        logger.info(f"   🔒 Salt: {salt[:16]}...")
        
        db.close()
        
        logger.info("")
        logger.info("═" * 60)
        logger.info("✅ Admin user successfully created and committed!")
        logger.info("═" * 60)
        logger.info("")
        logger.info("🔐 Login Credentials:")
        logger.info(f"   Phone: {admin_phone}")
        logger.info(f"   Password: {admin_password}")
        logger.info("")
        
        return True
        
    except Exception as e:
        logger.error("")
        logger.error(f"❌ Error creating admin user: {e}")
        logger.error("", exc_info=True)
        logger.error("")
        logger.error("═" * 60)
        logger.error("❌ Admin user creation failed!")
        logger.error("═" * 60)
        
        try:
            db.rollback()
            db.close()
        except:
            pass
        
        return False

if __name__ == "__main__":
    print("")
    success = create_admin_user()
    sys.exit(0 if success else 1)
