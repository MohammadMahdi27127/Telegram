"""
Alternative Admin User Seeding Script
This is a standalone version that can be run independently

Usage:
    cd backend
    python seed_admin.py
"""

import sys
import os
import time
import uuid
import hashlib
import secrets
import logging

# Setup logging with colors
logging.basicConfig(
    level=logging.INFO,
    format='%(message)s'
)
logger = logging.getLogger(__name__)

try:
    from sqlalchemy import create_engine
    from sqlalchemy.orm import sessionmaker
    from main import User, Base, get_database_url
    from config import settings
except ImportError as e:
    logger.error(f"❌ Import Error: {e}")
    logger.error("Make sure you run this from backend directory: cd backend && python seed_admin.py")
    sys.exit(1)

def hash_password(password: str, salt: str) -> str:
    """Hash password using SHA-256."""
    return hashlib.sha256(f"{salt}{password}".encode()).hexdigest()

def main():
    logger.info("")
    logger.info("╔═══════════════════════════════════════════════════════╗")
    logger.info("║      🔐 Admin User Seeding Script (Alternative)       ║")
    logger.info("╚═══════════════════════════════════════════════════════╝")
    logger.info("")
    
    admin_phone = "09052893115"
    admin_password = "138727127"
    admin_name = "محمد مهدی"
    
    try:
        # Database setup
        logger.info("📊 Database Configuration:")
        db_url = get_database_url(settings.DATABASE_URL)
        
        if "postgresql" in db_url:
            logger.info("   ✅ Using PostgreSQL")
        elif "sqlite" in db_url:
            logger.info("   ⚠️  Using SQLite (fallback)")
        
        logger.info("")
        logger.info("🔧 Setting up database engine...")
        engine = create_engine(db_url, echo=False, pool_pre_ping=True)
        
        logger.info("📋 Creating tables...")
        Base.metadata.create_all(bind=engine)
        logger.info("✅ Tables ready")
        
        logger.info("")
        logger.info("🔍 Checking for existing admin user...")
        SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
        db = SessionLocal()
        
        existing = db.query(User).filter(User.phone == admin_phone).first()
        
        if existing:
            logger.info(f"✅ Admin already exists!")
            logger.info(f"   Phone: {existing.phone}")
            logger.info(f"   Name: {existing.name}")
            logger.info(f"   Plan: {existing.plan}")
            db.close()
        else:
            logger.info("👨‍💼 Creating admin user...")
            
            salt = secrets.token_hex(16)
            pwd_hash = hash_password(admin_password, salt)
            
            admin = User(
                id=str(uuid.uuid4()),
                phone=admin_phone,
                salt=salt,
                password_hash=pwd_hash,
                name=admin_name,
                plan="admin",
                balance=1000000,
                suspended=False,
                banner_seen=True,
                created_at=int(time.time() * 1000),
                username="admin"
            )
            
            db.add(admin)
            db.commit()
            db.refresh(admin)
            
            logger.info("✅ Admin created successfully!")
            logger.info(f"   ID: {admin.id}")
            logger.info(f"   Phone: {admin.phone}")
            logger.info(f"   Name: {admin.name}")
            logger.info(f"   Plan: {admin.plan}")
            logger.info(f"   Balance: {admin.balance:,} tomans")
            
            db.close()
        
        logger.info("")
        logger.info("═" * 60)
        logger.info("✅ Admin seeding completed successfully!")
        logger.info("═" * 60)
        logger.info("")
        logger.info("🔐 Test Credentials:")
        logger.info(f"   Phone: {admin_phone}")
        logger.info(f"   Password: {admin_password}")
        logger.info("")
        
        return 0
        
    except Exception as e:
        logger.error("")
        logger.error(f"❌ Error: {e}")
        logger.error("")
        
        import traceback
        traceback.print_exc()
        
        return 1

if __name__ == "__main__":
    exit_code = main()
    sys.exit(exit_code)
