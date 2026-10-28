"""
Create admin user in database
Admin: 09052893115, Password: 138727127, Name: محمد مهدی
"""

import sys
import time
import uuid
import hashlib
import secrets
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

# Import database models from main.py
try:
    from main import User, Base, engine
except ImportError:
    print("Error: Could not import from main.py")
    sys.exit(1)

def hash_password(password: str, salt: str) -> str:
    """Hash password with salt using SHA-256"""
    return hashlib.sha256(f"{salt}{password}".encode()).hexdigest()

def create_admin_user():
    """Create admin user in database"""
    
    # Admin credentials
    admin_phone = "09052893115"
    admin_password = "138727127"
    admin_name = "محمد مهدی"
    
    # Create session
    SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
    db = SessionLocal()
    
    try:
        # Check if admin already exists
        existing_admin = db.query(User).filter(User.phone == admin_phone).first()
        
        if existing_admin:
            print(f"✅ Admin user already exists!")
            print(f"   Phone: {existing_admin.phone}")
            print(f"   Name: {existing_admin.name}")
            print(f"   Plan: {existing_admin.plan}")
            
            # Update if needed
            if existing_admin.plan != "admin":
                existing_admin.plan = "admin"
                db.commit()
                print(f"   Updated plan to: admin")
            
            return
        
        # Create new admin user
        salt = secrets.token_hex(16)
        password_hash = hash_password(admin_password, salt)
        
        admin_user = User(
            id=str(uuid.uuid4()),
            phone=admin_phone,
            salt=salt,
            password_hash=password_hash,
            name=admin_name,
            plan="admin",  # Admin plan
            balance=1000000,  # 1 million tomans balance
            suspended=False,
            banner_seen=True,
            created_at=int(time.time() * 1000),
            username="admin"
        )
        
        db.add(admin_user)
        db.commit()
        
        print("✅ Admin user created successfully!")
        print(f"   ID: {admin_user.id}")
        print(f"   Phone: {admin_user.phone}")
        print(f"   Name: {admin_user.name}")
        print(f"   Plan: {admin_user.plan}")
        print(f"   Balance: {admin_user.balance:,} tomans")
        print(f"   Username: @{admin_user.username}")
        
    except Exception as e:
        print(f"❌ Error creating admin user: {e}")
        db.rollback()
    finally:
        db.close()

if __name__ == "__main__":
    print("═" * 60)
    print("Creating admin user...")
    print("═" * 60)
    
    # Create tables if they don't exist
    try:
        Base.metadata.create_all(bind=engine)
        print("✅ Database tables verified")
    except Exception as e:
        print(f"⚠️  Warning: Could not verify tables: {e}")
    
    create_admin_user()
    
    print("═" * 60)
