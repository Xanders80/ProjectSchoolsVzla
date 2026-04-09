// Terraform Scaffolder - DevOps Tool for SMS
// Generates Terraform configurations for SMS infrastructure

export interface TerraformConfig {
  provider: string;
  region: string;
  instanceType: string;
  environment: string;
}

export class TerraformScaffolder {
  generateMain(config: TerraformConfig): string {
    return `terraform {
  required_version = ">= 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "${config.region}"
}

variable "environment" {
  default = "${config.environment}"
}

variable "app_name" {
  default = "school-management"
}
`;
  }

  generateEC2(config: TerraformConfig): string {
    return `resource "aws_instance" "sms_app" {
  ami           = data.aws_ami.ubuntu.id
  instance_type = "${config.instanceType}"

  tags = {
    Name        = "\${var.app_name}-\${var.environment}"
    Environment = var.environment
    Application = "school-management"
  }
}

data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"]

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"]
  }
}
`;
  }

  generateRDS(): string {
    return `resource "aws_db_instance" "sms_db" {
  identifier     = "sms-\${var.environment}"
  engine         = "mariadb"
  engine_version = "11.0"
  instance_class = "db.t3.medium"

  allocated_storage     = 20
  max_allocated_storage = 100
  storage_type          = "gp3"

  db_name  = "dbSchollAdm"
  username = "sms_admin"
  password = var.db_password

  backup_retention_period = 7
  backup_window          = "03:00-04:00"
  maintenance_window     = "sun:04:00-sun:05:00"

  skip_final_snapshot = true

  tags = {
    Name        = "sms-db-\${var.environment}"
    Environment = var.environment
  }
}

variable "db_password" {
  sensitive = true
}
`;
  }
}
