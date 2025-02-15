# module "ecs" {
#   source = "terraform-aws-modules/ecs/aws"

#   cluster_name       = var.environment
#   container_insights = true
#   capacity_providers = ["FARGATE"]
#   default_capacity_provider_use_fargate = [
#     {
#       capacity_provider = "FARGATE"
#     }
#   ]
# }

module "ecs" {
  source = "terraform-aws-modules/ecs/aws"

  cluster_name = var.environment

  cluster_configuration = {
    execute_command_configuration = {
    }
  }

  fargate_capacity_providers = {
    FARGATE = {
      default_capacity_provider_strategy = {
        weight = 1
      }
    }
  }
}

resource "aws_ecs_task_definition" "Java-Spring-API" {
  family                   = "Java-Spring-API"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = 256
  memory                   = 512
  execution_role_arn       = aws_iam_role.role.arn
  container_definitions = jsonencode(
    [
      {
        name      = var.container_name
        image     = var.image_name
        cpu       = 256
        memory    = 512,
        essential = true
        portMappings = [
          {
            containerPort = 8080,
            hostPort      = 8080
          }
        ]
      }
    ]
  )
}

resource "aws_ecs_service" "Java-Spring-API" {

  depends_on = [aws_lb_target_group.target]

  name            = "Java-Spring-API"
  cluster         = module.ecs.cluster_id
  desired_count   = 1
  task_definition = aws_ecs_task_definition.Java-Spring-API.arn

  load_balancer {
    target_group_arn = aws_lb_target_group.target.arn
    container_name   = "homologacao"
    container_port   = 8080
  }

  network_configuration {
    subnets          = module.vpc.private_subnets
    security_groups  = [aws_security_group.private.id]
    assign_public_ip = true
  }

  capacity_provider_strategy {
    capacity_provider = "FARGATE"
    weight            = 1
  }
}
